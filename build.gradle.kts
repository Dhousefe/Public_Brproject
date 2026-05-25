import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar 
import org.gradle.api.tasks.Copy 
import org.gradle.api.tasks.JavaExec 
import org.gradle.api.Task
import java.time.Instant
import java.text.SimpleDateFormat
import java.util.Date

// 1. Plugins
plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm") version "2.3.0-Beta2" 
}

// 2. Repositórios
repositories {
    mavenCentral()
    google()
    mavenLocal()
    maven { url = uri("https://artifacts.deepl.com/maven/") } 
}

// Define o caminho 'build/classes' para unificar classes Java e Kotlin (igual ao Ant)

val antClassesDir = layout.buildDirectory.dir("classes")

// 3. Estrutura de Pastas
sourceSets {
    main {
        java.srcDirs("java") 
        kotlin.srcDirs("kotlin") 
        kotlin {
            // As exclusões permanecem
        }
    }
}

// 3.5. Configuração de Compilação Híbrida (Compatibilidade Máxima)

// Configura JVM Toolchain para Java 25 (garante que Kotlin e Java usem JDK 25)
kotlin {
    jvmToolchain(25)
}

tasks.withType<JavaCompile> {
    // Apenas main compila para build/classes; test usa saída padrão (evita conflito com syncBinClasses)
    if (name == "compileJava") {
        destinationDirectory.set(antClassesDir)
    }
    // Evita cache que não restaura classes no build/classes
    outputs.cacheIf { false }
}

tasks.withType<KotlinCompile>().all {
    // Apenas main compila para build/classes; test usa saída padrão (evita conflito com syncBinClasses)
    if (name == "compileKotlin") {
        destinationDirectory.set(antClassesDir)
        // Garante que o diretório existe antes da compilação
        doFirst {
            antClassesDir.get().asFile.mkdirs()
        }
    }
    // Evita cache que não restaura classes no build/classes
    outputs.cacheIf { false }
    
    // Usa compilerOptions (nova API recomendada)
    compilerOptions {
        // Kotlin 2.3.0-Beta2+ suporta JVM target 25
        // Usa JVM_25 diretamente (suportado nesta versão)
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
        freeCompilerArgs.addAll(
            "-Xno-call-assertions", 
            "-Xno-param-assertions", 
            "-Xno-receiver-assertions"
        )
    }
}


// O Gradle será forçado a compilar o Kotlin primeiro devido a essa dependência.
tasks.named<JavaCompile>("compileJava") {
    dependsOn(tasks.named("compileKotlin"))
    // Garante que copyDependencies execute antes (para ter JARs disponíveis no classpath)
    tasks.findByName("copyDependencies")?.let {
        dependsOn(it)
    }
    val kotlinCompile = tasks.named<KotlinCompile>("compileKotlin").get()
    // Classpath para ver classes Kotlin: prioriza saída do Kotlin (CoroutinePool, LoginServerThread, etc.)
    classpath = files(antClassesDir.get().asFile) + files(kotlinCompile.destinationDirectory) + files(kotlinCompile.outputs.files) + classpath
}


// 4. Configuração do Java/Kotlin
// Kotlin 2.3.0-Beta2+ suporta JVM target 25 nativamente
// Ambos Java e Kotlin compilam para JVM 25 com suporte completo
java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

// 5. Dependências
dependencies {
    // DEPENDÊNCIAS DO KOTLIN
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.3.0-Beta2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0") 
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.3.0-Beta2")

    // --- Suas Bibliotecas ---
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.0")
    implementation("com.zaxxer:HikariCP:5.1.0")
    // Cap'n Proto
    implementation("org.capnproto:runtime:0.1.16")
    
    // Zstd Compression
    implementation("com.github.luben:zstd-jni:1.5.6-3")

    implementation(kotlin("stdlib"))
    
    // Fast Collections (FastUtil)
    implementation("it.unimi.dsi:fastutil-core:8.5.18")
    
    
    
    // Dependências locais da pasta libs (igual ao Ant)
    // O Ant usa ${src-lib} que aponta para "libs"
    // IMPORTANTE: Estes JARs já existem e não são gerados pelo copyDependencies
    // Usamos fileTree para evitar problemas de dependência de tasks
    implementation(fileTree("libs") {
        include("DeepL.jar")
        include("license.jar")
        include("mariadb.jar")
        include("c3p0-0.9.5-pre5.jar")
        include("mchange-commons-java-0.2.6.2.jar")
        
    })
}

// 6. TASK CUSTOMIZADA: Criptografia de Classes 
tasks.register<JavaExec>("encryptCryptaClasses") {
    dependsOn(tasks.classes) 
    group = "security"
    description = "Criptografa as classes do pacote ext.mods.Crypta"
    classpath = sourceSets.getByName("main").runtimeClasspath + files(antClassesDir) 
    mainClass.set("ext.mods.util.ClassEncryptor")
    args("crypta")
}


tasks.register<Jar>("buildSecurityTools") {
    dependsOn(tasks.named("compileKotlin"), tasks.named("compileJava"))
    tasks.findByName("compileTestKotlin")?.let { 
        mustRunAfter(it)
    }
    tasks.findByName("compileTestJava")?.let { 
        mustRunAfter(it)
    }
    group = "security"
    archiveBaseName.set("security-tools")
    destinationDirectory.set(file("libs"))
    // Usa apenas as classes compiladas do sourceSet main (não test)
    from(sourceSets.main.get().output.classesDirs) {
        include("ext/mods/util/SecureKeyManager.class")
        include("ext/mods/util/SecureConfig.class")
        include("ext/mods/util/ClassEncryptor.class")
        include("ext/mods/util/Util.class") 
    }
    manifest {
        attributes("Main-Class" to "ext.mods.util.SecureKeyManager")
    }
}

// 8. Configuração do JAR principal (O 'server.jar')
tasks.jar {
    dependsOn(tasks.named("compileJava"))
    mustRunAfter(tasks.named("syncBinClasses"))
    
    // Usando a referência simples que funciona na maioria dos ambientes
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE 
    
    // Filtra as dependências locais e expande o restante (Fat JAR)
    from(configurations.runtimeClasspath.get().map { 
        val path = it.absolutePath
        if (it.isDirectory) it 
        else if (path.endsWith(".jar") && path.contains("lib")) it 
        else zipTree(it) 
    })
    
    // Inclui os JARs locais da pasta libs (igual ao Ant)
    // O Ant usa ${src-lib} que aponta para "libs"
    from(files("libs/c3p0-0.9.5-pre5.jar").filter { it.exists() })
    from(files("libs/DeepL.jar").filter { it.exists() })
    from(files("libs/license.jar").filter { it.exists() })
    from(files("libs/mariadb.jar").filter { it.exists() })
    
    // Exclui arquivos de metadados redundantes que causam o erro DuplicatesStrategy
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/LICENSE*", "META-INF/NOTICE*", "META-INF/services/*", "META-INF/versions/**")
    
    manifest {
        attributes(mapOf("Main-Class" to "ext.mods.gameserver.GameServer", "Build-Date" to Instant.now().toString()))
    }
    archiveBaseName.set("server")
    destinationDirectory.set(file("libs")) 
    
    
    from(antClassesDir)
}

// 9. TASK CUSTOMIZADA: Copia as dependências 
// IMPORTANTE: Não sobrescreve JARs locais existentes (DeepL.jar, license.jar, etc)
tasks.register<Copy>("copyDependencies") {
    group = "distribution"
    description = "Copia dependências Maven para a pasta /libs e pasta crypta para Brproject_Distribution"
    
    // Copia JARs do Maven para /libs
    from(configurations.runtimeClasspath.get().filter { 
        // Apenas copia JARs do Maven, não os JARs locais já existentes
        val fileName = it.name
        !fileName.contains("DeepL.jar") &&
        !fileName.contains("license.jar") &&
        !fileName.contains("mariadb.jar") &&
        !fileName.contains("c3p0") &&
        !fileName.contains("mchange")
    })
    into(file("libs"))
    
    // Não sobrescreve arquivos existentes
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// 9.2. TASK: Sincroniza classes do Gradle para a pasta bin (IDE)
tasks.register<Copy>("syncBinClasses") {
    group = "distribution"
    description = "Copia classes compiladas (build/classes) para bin/"
    dependsOn(tasks.named("compileKotlin"), tasks.named("compileJava"))
    from(antClassesDir)
    into(file("bin/main"))
}

// 9.1. TASK CUSTOMIZADA: Copia pasta crypta para Brproject_Distribution
tasks.register<Copy>("copyCrypta") {
    group = "distribution"
    description = "Copia pasta crypta de game/data/prevention para Brproject_Distribution"
    
    from(file("game/data/prevention/crypta")) {
        include("**/*")
        // Inclui arquivos criptografados, metadados e chaves
        include("**/*.encrypted")
        include("**/*.meta")
        include("**/key.properties")
    }
    into(file("Brproject_Distribution/game/data/prevention/crypta"))
    
    // Não sobrescreve arquivos existentes
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    
    // Só executa se a pasta source existir
    onlyIf { file("game/data/prevention/crypta").exists() }
}

// 10. Configuração do Build Principal 
tasks.build {
    dependsOn(tasks.named("compileKotlin"))
    dependsOn(tasks.named("compileJava"))
    dependsOn(tasks.named("jar"))
	dependsOn(tasks.named("PrepararTeste"))
    dependsOn(tasks.named("syncBinClasses"))
}



// 12. Configuração para garantir que compileKotlin use build/classes após clean
tasks.named("compileKotlin") {
    doFirst {
        val outputDir = antClassesDir.get().asFile
        if (!outputDir.exists()) {
            outputDir.mkdirs()
            logger.lifecycle("Diretório de saída criado: ${outputDir.absolutePath}")
        }
        logger.lifecycle("Compilando Kotlin para: ${outputDir.absolutePath}")
    }
}


// 13. PrepararTeste (Distribuição de Teste)
tasks.register("PrepararTeste") {
	dependsOn(tasks.named("compileJava"))
    mustRunAfter(tasks.named("jar"))
    group = "distribution"
    description = "Cria uma distribuicao de teste rapida em Brproject_Distribution (Dev Teste)"
    

    doLast {
        val buildZip = file("Brproject_Distribution")
        
        
        val now = Date()
        val dStamp = SimpleDateFormat("yyyyMMdd").format(now)
        val tStamp = SimpleDateFormat("HHmm").format(now)

        println("=== BUILD DE TESTE - APENAS ARQUIVOS ESSENCIAIS ===")
        println("Mantendo: libs/, login/, images/, sound/, Hwid/, tools/")
        println("Criando em: ${buildZip.absolutePath}")

        
        println("Removendo caches AppCDS (game/login)...")
        project.delete(
            "game/cache", "login/cache", "game/log", "login/log",
            "${buildZip}/game/cache", "${buildZip}/login/cache", 
            "${buildZip}/game/log", "${buildZip}/login/log"
        )

        // Configurações do Game
        project.copy {
            from("game/config")
            into("${buildZip}/game/config")
            include("**/*.properties", "**/*.ini")
        }

        // Bibliotecas
        project.copy {
            from("libs")
            into("${buildZip}/libs")
            include("**/*.jar")
        }

        // Login Server (excluindo cache)
        project.copy {
            from("login")
            into("${buildZip}/login")
            exclude("cache/**")
        }

        // Diretórios estáticos copiados integralmente
        val staticDirs = listOf("images", "sound", "Hwid", "tools")
        for (dir in staticDirs) {
            project.copy {
                from(dir)
                into("${buildZip}/$dir")
            }
        }

        // Scripts e arquivos raiz
        project.copy {
            from(".")
            into(buildZip)
            include(
                "StartGame_SemDashboard.bat",
                "StartLogin_SemDashboard.bat",
                "README.md",
                "Dockerfile",
                "entrypoint.sh"
            )
        }

        // Cache Scripts
        project.copy {
            from("cache")
            into("${buildZip}/cache")
            include("*.inc.bat")
        }

        // 4. Criação do arquivo BUILD_INFO.txt
        val buildInfoFile = file("${buildZip}/BUILD_INFO.txt")
        buildInfoFile.writeText("""
            === BUILD DE TESTE ===
            Data: $dStamp $tStamp
            Tipo: Teste (sem arquivos de dados pesados)

            ARQUIVOS INCLUÍDOS:
            - libs/ (todas as bibliotecas)
            - login/ (servidor de login)
            - images/ (imagens da interface)
            - sound/ (sons)
            - Hwid/ (proteção HWID)
            - tools/ (ferramentas)
            - game/config/ (configurações)

            ARQUIVOS EXCLUÍDOS (para reduzir tamanho):
            - game/data/custom/ (mods customizados)
            - game/data/geodata/ (dados geográficos)
            - game/data/locale/ (arquivos de idioma)
            - game/data/xml/ (arquivos XML)
        """.trimIndent())

        println("=== BUILD CONCLUiDO ===")
        println("Diretorio: ${buildZip.name}")
        println("Arquivos para teste rapido.")
    }
}