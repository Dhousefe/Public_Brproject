# BrProject - Emulador Lineage 2 Interlude (C6)

![BrProject Banner](https://img.shields.io/badge/Lineage%202-Interlude%20C6-blue)
![Java](https://img.shields.io/badge/Java-25-orange)
![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0--Beta2-purple)
![License](https://img.shields.io/badge/License-GPL%20v3.0-green)
![Contribuições Dev A.L.N](https://img.shields.io/badge/Contribuições-9%20correções-brightgreen)

**Emulador de servidor Lineage 2 Interlude (C6)** desenvolvido em Java/Kotlin, com foco em performance, segurança e extensibilidade. Baseado na comunidade L2JBrasil, com arquitetura moderna utilizando Kotlin Coroutines, Netty, e JDK 25.

---

## 🎯 Visão Geral

O BrProject é um emulador completo de servidor de Lineage 2, composto por:

- **GameServer** — servidor principal do jogo (porta 7777)
- **LoginServer** — servidor de autenticação (porta 2106)
- **Dashboard/Launcher** — interface GUI (Swing) para gerenciamento e licenciamento
- **Sistema de Extensões** — carregamento dinâmico de mods via ExtensionLoader

---

## 👥 Equipe de Desenvolvimento

### **Core Team:**
- **Dhousefe-L2JBR** - Líder do projeto
- **Agazes33** - Desenvolvimento core
- **Ban-L2jDev** - Sistema de segurança
- **Warman** - Gameplay e balanceamento
- **SrEli** - Database e SQL
- **Dev ⩿ A.L.N/⪀** - Testes, correções, otimizações e manutenção

### **Colaboradores:**
Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo, Tiagorosendo, Schuster, LucasStark, damedd

---

## 🏆 Minhas Contribuições (Dev ⩿ A.L.N/⪀)

### **📋 Lista Completa de Correções**

#### 1. **Auto Farm Visual** *(09/07/2026)*
**Problema:** Círculo amarelo aparecia nas paredes das catacombs e em área aberta, distraindo a jogabilidade.
**Solução:** Remoção completa da visualização do círculo amarelo mantendo toda a funcionalidade.
**Arquivos corrigidos (6):**
- `MovementIntegration.java` - `visualizeFarmLimit()` desabilitado
- `ZoneBuilder.java` - `previewCylinderCalc()` e `previewCylinder()` desabilitados
- `ZoneCylinderZ.java` - `visualizeZone()` desabilitado (arquivo crítico)
- `AutoFarmRoute.java` - `visualizeZone()` desabilitado
- `AutoFarmZone.java` - `visualizeZone()` desabilitado
- `AutoFarmManager.java` - Previews e task periódica desabilitados
**Resultado:** ✅ Auto farm funciona "silenciosamente" sem distrações visuais.

#### 2. **Sistema PIX** *(Correção de distribuição)*
**Problema:** Arquivo `custom/Pix.properties` faltando na distribuição, deixando o sistema PIX desativado.
**Solução:** Adição do arquivo de configuração PIX com token de teste e configuração básica.
**Arquivos corrigidos:**
- `game/config/custom/Pix.properties` - Configuração do sistema PIX
**Resultado:** ✅ Sistema PIX ativado na distribuição Brproject_Distribution.

#### 3. **Comando .pack (HTML no chat)** *(Otimização de interface)*
**Problema:** Tags HTML apareciam como texto cru no chat do L2.
**Solução:** Remoção de HTML e CreatureSay; tudo vai por sendMessage simples.
**Arquivos corrigidos:**
- `Pack.java` - Chat .pack com texto puro
**Resultado:** ✅ Comando .pack funciona corretamente sem tags HTML visíveis.

#### 4. **Fluxo Launcher (Boot)** *(Otimização de inicialização)*
**Problema:** Fluxo complexo entre launcher público e revisão.
**Solução:** Documentação e padronização do fluxo:
- RAIZ: `Start.exe` + `Start.lnk` (único launcher público)
- REVISAO: `Start.lnk` → `cache\launcher-panel.cmd`
- Painel: banner 10s no CMD → javaw + janela roxa
**Resultado:** ✅ Fluxo de inicialização padronizado e documentado.

#### 5. **HTML Data Path** *(Organização de arquivos)*
**Problema:** HTML distribuído em múltiplas pastas locale.
**Solução:** Centralização do HTML em `data/html/` estilo L2J clássico.
**Arquivos corrigidos:**
- HTML movido para `data/html/`
- Locale mantido apenas para `sysstring.xml`
**Resultado:** ✅ Estrutura de arquivos HTML organizada e padronizada.

#### 6. **Pack Cores** *(Sistema de cores)*
**Problema:** Sistema de cores inconsistente no pack.
**Solução:** Implementação de sistema de cores unificado.
**Arquivos corrigidos:**
- Sistema de cores do pack
**Resultado:** ✅ Sistema de cores consistente em todo o projeto.

#### 7. **Geodata** *(Otimização de performance)*
**Problema:** Processamento de geodata ineficiente.
**Solução:** Implementação de:
- `geo-index/` - Indexação otimizada
- `hdpngen/` - Gerador de pathfinding
- `regioes-stub/` - Stubs de regiões
- `spawn-*/` - Spawns otimizados por região
**Resultado:** ✅ Performance melhorada no processamento de geodata.

#### 8. **Locale (pt-BR)** *(Localização)*
**Problema:** Localização incompleta para português brasileiro.
**Solução:** Implementação completa de locale pt-BR:
- `country-detect/` - Detecção de país
- `pt-br/` - Traduções completas
**Resultado:** ✅ Suporte completo ao português brasileiro.

#### 9. **Build System** *(Otimização de compilação)*
**Problema:** Processo de build inconsistente.
**Solução:** Documentação e padronização do sistema de build.
**Resultado:** ✅ Processo de build documentado e otimizado.

---

## 📊 Resumo das Contribuições

| Área | Correções | Status |
|------|-----------|--------|
| Gameplay Visual | 6 arquivos Java | ✅ Completamente corrigido |
| Sistemas de Pagamento | 1 sistema (PIX) | ✅ Ativado na distribuição |
| Interface Chat | 1 comando (.pack) | ✅ Otimizado |
| Inicialização | Fluxo launcher | ✅ Documentado |
| Organização Arquivos | HTML data path | ✅ Centralizado |
| Sistema de Cores | Pack cores | ✅ Unificado |
| Performance | Geodata | ✅ Otimizado |
| Localização | pt-BR locale | ✅ Completado |
| Build System | Processo de build | ✅ Documentado |

**Total: 9 áreas corrigidas/otimizadas**

---

## ⚙️ Requisitos do Sistema

- **JDK 25** (Eclipse Adoptium recomendado)
- **MariaDB** (driver 3.4.0 incluído)
- **Gradle 8+** (wrapper incluído)
- **Windows 10+** ou **Linux** (Docker disponível)
- ~2 GB RAM mínimo para GameServer

---

## 📁 Estrutura do Projeto

```
Brproject/
├── java/                    # Código-fonte Java (2842 arquivos)
│   └── ext/mods/
│       ├── gameserver/      # Core do GameServer
│       │   ├── model/       # Atores, itens, mundo, zonas
│       │   ├── network/     # Pacotes cliente/servidor (207 client, 282 server)
│       │   ├── handler/     # Admin, bypass, chat, item, skill, voice commands
│       │   ├── scripting/   # Quests (343+), tasks, scripts de teleport
│       │   ├── skills/      # Efeitos, condições, fórmulas de combate
│       │   ├── data/        # Managers, XML parsers, SQL tables
│       │   ├── geoengine/   # Geodata, pathfinding cache
│       │   ├── communitybbs/# Community Board (BBS)
│       │   ├── custom/      # Dados customizados (balance, donate, PvP, etc.)
│       │   └── enums/       # Enumerações do jogo
│       ├── loginserver/     # LoginServer, GameServerThread
│       ├── security/        # Licenciamento, GUI launcher
│       ├── protection/      # HWID, criptografia de pacotes (VMPC)
│       ├── Crypta/          # Classes criptografadas (proteção de código)
│       ├── fakeplayer/      # Sistema de FakePlayers
│       ├── dungeon/         # Sistema de Dungeons (Kamaloka)
│       ├── sellBuffEngine/  # Sistema de venda de buffs
│       ├── tour/            # Sistema de torneios
│       ├── BossZerg/        # Anti-zerg para boss raids
│       └── ...              # Outros mods (agathion, dressme, roulette, etc.)
├── kotlin/                  # Código-fonte Kotlin (34 arquivos)
│   └── ext/mods/
│       ├── commons/pool/    # CoroutinePool, ThreadProvider
│       └── gameserver/
│           ├── GameServer.kt        # Entry-point principal
│           ├── LoginServerThread.kt # Comunicação Login<->Game
│           ├── geoengine/           # GeoEngine, PathFinder avançado
│           └── model/actor/move/    # Sistema de movimentação (Player, NPC, Summon)
└── ... (estrutura completa no arquivo original)
```

---

## 🛠️ Tecnologias e Dependências

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| Kotlin | 2.3.0-Beta2 | GameServer core, GeoEngine, Pathfinding |
| Java | 25 | Maior parte do codebase |
| Kotlin Coroutines | 1.9.0 | Carregamento paralelo, thread pool |
| Netty | 4.1.107 | Networking de alta performance |
| MariaDB JDBC | 3.4.0 | Conexão com banco de dados |
| HikariCP | 5.1.0 | Connection pooling |
| FastUtil | 8.5.18 | Coleções primitivas otimizadas |
| LMAX Disruptor | 3.4.4 | Ring buffer para eventos |
| Zstd | 1.5.6 | Compressão de dados |
| Cap'n Proto | 0.1.16 | Serialização de alto desempenho |
| DeepL API | - | Tradução automática em runtime |

---

## 🏗️ Build

### Gradle (recomendado)
```bash
# Build completo (compila + gera server.jar + sincroniza bin/)
./gradlew build

# Apenas compilar
./gradlew compileJava compileKotlin

# Gerar server.jar (fat JAR)
./gradlew jar

# Copiar dependências Maven para libs/
./gradlew copyDependencies

# Gerar distribuição de teste
./gradlew PrepararTeste

# Criptografar classes sensíveis
./gradlew encryptCryptaClasses

# Build de security-tools.jar
./gradlew buildSecurityTools
```

### Ant (build legado)
```bash
# Distribuição segura (com criptografia)
ant -f Mount.xml dist-secure

# Distribuição de teste (leve)
ant -f Mount.xml dist-test

# Gerar arquivo 7z
ant -f Mount.xml dist-7z
```

---

## 🚀 Execução

### Windows
```batch
REM Inicia via Launcher (com GUI e licenciamento)
StartBrproject.bat

REM Inicia Game sem dashboard
StartGame_SemDashboard.bat

REM Inicia Login sem dashboard
StartLogin_SemDashboard.bat

REM Registrar GameServer
RegisterGameServer.bat
```

### Docker
```bash
docker build -t brproject .

docker run -d -p 7777:7777 -p 2106:2106 \
  -e L2_EMAIL="seu@email.com" \
  -e PASSWORD="suasenha" \
  brproject
```

O container executa LoginServer e GameServer simultaneamente com flags JVM otimizadas para G1GC, AppCDS e Compact Object Headers (JDK 25+).

### Linux
O script `entrypoint.sh` pode ser usado diretamente:
```bash
chmod +x entrypoint.sh
./entrypoint.sh
```

---

## ⚙️ Configuração

Os arquivos de configuração ficam em `game/config/`:

| Arquivo | Descrição |
|---------|-----------|
| `server.properties` | IP, portas, limites de conexão |
| `rates.properties` | Taxas de XP, SP, drop, adena |
| `players.properties` | Configurações de jogadores |
| `npcs.properties` | Comportamento de NPCs e mobs |
| `events.properties` | Eventos (TvT, DM, CTF, LM) |
| `geoengine.properties` | Geodata e pathfinding |
| `mods.properties` | Toggle de mods customizados |
| `protection.properties` | HWID, anti-cheat |
| `bosszerg.properties` | Anti-zerg em boss raids |
| `kamaloka.properties` | Sistema de dungeons |
| `offlineshop.properties` | Lojas offline |
| `items.properties` | Enchant, drop, crafting |
| `clans.properties` | Sistema de clãs |
| `siege.properties` | Configurações de siege |

O LoginServer usa `login/config/loginserver.properties`.

---

## 🗃️ Banco de Dados

O projeto usa MariaDB. Os 86 scripts SQL em `tools/sql/` criam o schema necessário:

```bash
# Windows
tools/install_db.bat

# Linux
tools/install_db.sh
```

---

## 🎮 Sistemas e Mods Customizados

### Gameplay
- **AutoFarm** — sistema completo com rotas, skills, e controle de tempo *(corrigido por Dev ⩿ A.L.N/⪀)*
- **Fake Players** — bots de PvP com equipes e nomes configuráveis
- **Dungeons (Kamaloka)** — instâncias com templates XML
- **Torneios** — eventos PvP automatizados
- **Boss Zerg Protection** — anti-zerg com flag PvP automático
- **Level Up Maker** — progressão customizada de level

### Economia
- **Sell Buff Engine** — venda de buffs entre jogadores
- **Auction House** — leilões via Community Board
- **Donate System** — integração com sistema de doações
- **Capsule Box** — caixas de recompensas
- **Roulette** — roleta de prêmios

### Social
- **Community Board (BBS)** — fórum in-game, mail, favoritos
- **DressMe** — sistema de aparência/skin
- **Agathion** — companheiros com efeitos de teleport
- **Player God** — sistema de deificação

### Combate & Balance
- **Balance por Classe** — ajuste fino de dano/defesa por ClassId
- **PvP Data** — rankings e recompensas PvP
- **Enchant Data** — taxas e limites de enchant customizados
- **Equipment Grade Restriction** — restrições por grau de equip
- **Polymorph** — transformações

## PixMod (Mod PIX / Donation — Dev A.L.N.)

O **PixMod** é um módulo completo de **doações in-game** com integração a múltiplos gateways de pagamento (PIX, PayPal, Binance/Crypto), cotações de moeda em tempo real, fila assíncrona de pagamentos, envio de e-mail automático e bypass multi-idioma. Está implementado em 21 arquivos sob `java/ext/mods/PixMod/`.

### Estrutura de Pacotes

```
ext/mods/PixMod/
├── donation/
│   ├── DonationAnnounce.java        # broadcast global ao receber item doado
│   └── DonationBypassAdapter.java   # roteador de bypass (pix / pay / donation)
└── donationmanager/
    ├── DonationManager.java         # orquestrador (singleton, 1300+ linhas)
    ├── CheckoutChoice.java          # seleção dinâmica de métodos
    ├── CurrencyManager.java         # cotações fiat + crypto (Binance API)
    ├── DonationData.java            # tabela em memória de compras ativas
    ├── DDSConverter.java            # parse de QR-code PIX (BR Code / EMV)
    ├── Mailersend.java              # envio de e-mail via API MailerSend
    ├── IPaymentHandler.java         # interface comum dos gateways
    ├── paymenthandlers/
    │   ├── MercadoPago.java         # PIX dinâmico + link de pagamento
    │   ├── binance/{Binance,Order}.java  # pagamento em cripto
    │   └── paypal/{PayPal,Invoice}.java  # PayPal link/invoice
    ├── purchase/
    │   ├── PaymentMethod.java       # enum MP_PIX | MP_LINK | PAYPAL | BINANCE
    │   ├── Purchase.java            # registro imutável de compra
    │   └── PurchaseStatus.java      # CREATED, PENDING, WAITING, PAID, ...
    └── tasks/
        ├── DonationTaskManager.java # scheduler das tasks de cotação
        ├── CryptoCurrencyTask.java  # cotação cripto (AwesomeAPI ou CoinBase)
        ├── FiatCurrencyTask.java    # cotação fiat (AwesomeAPI)
        └── DonationAutomaticPaymentTask.java  # polling dos pagamentos pendentes
```

### Ativação (master switch)

Tudo dentro do PixMod é controlado por duas flags em `mods.properties` (ou `language.properties`/`donation.properties`):

```properties
ENABLE_PIX_MOD=true
DONATION_ENABLED=true
```

A combinação `ENABLE_PIX_MOD && DONATION_ENABLED` é checada em cada bypass (`DonationBypassAdapter.tryHandle`) e na inicialização do `DonationManager` (`GameServer.kt` chama `net.sf.donationmanager.DonationManager.getInstance()` no boot).

### Comandos do Jogador

| Comando | HTML servido | Locale de UI |
|---|---|---|
| `.pix` | `game/data/html/mods/donation/pt/` | `pt` |
| `.pay` | `game/data/html/mods/donation/en/` | `en` |
| `.donation` | `game/data/html/mods/donation/` (genérico) | `pt` por padrão |

Os comandos são registrados no `BypassHandler` / `RequestBypassToServer` e roteados por `DonationBypassAdapter.tryHandle()`. Bypass suporta prefixo opcional `-h` (HTML legacy) e aceita formas antigas (`bypass pix htm index.htm`) que são normalizadas automaticamente.

### Fluxo de uma Doação

1. Jogador digita `.pix` → `BypassHandler` chama `DonationBypassAdapter.tryHandle(player, "pix")` → define `donation_html_locale=pt` no `Player.getMemos()` → `DonationManager.handleBypass(player, "index")`.
2. `DonationManager` envia `NpcHtmlMessage(HTML_ID=9999)` com `index_single.htm`, mostrando métodos de pagamento ativos (de `Config.DONATION_MP_PIX` / `_LINK` / `_PAYPAL_LINK` / `_BINANCE_PAY`).
3. Jogador escolhe quantidade + método → bypass `donation checkoutChoice <method> <qnt>`.
4. `DonationManager.checkoutChoice()`:
   - **PIX** (`MP_PIX`): gera BR Code via `DDSConverter`, registra `Purchase(PENDING, PIX_CODE, EXPIRATION_TIME)` e envia e-mail (se `DONATION_MP_PIX_MAIL=true`).
   - **Link MP** (`MP_LINK`): cria preferência no Mercado Pago, retorna URL de checkout.
   - **PayPal**: cria invoice (`PayPal.createOrder`) com `DONATION_PAYPAL_PRICE` × quantidade.
   - **Binance**: lista crypto aceitas + cotação em tempo real de `CurrencyManager`.
5. `DonationAutomaticPaymentTask` (scheduled, `DONATION_CHECK_TIME` ms) faz polling dos pagamentos com status `WAITING/PENDING`, atualiza para `PAID/CANCELLED`, e ao pagar:
   - Envia item `DONATION_PURCHASABLE_ITEM` × quantidade via `MailManager` ou `Player.addItem`.
   - Se `ANNOUNCE_DONATOR_ITEM_GLOBAL=true`, chama `DonationAnnounce.broadcast(player, item)`.
   - Envia e-mail de confirmação (`Mailersend`).
   - Aplica regras de limpeza (`DONATION_DELETE_PAYMENT_DATA` / `DONATION_DELETE_INACTIVE`).

### Gateways de Pagamento Suportados

| Gateway | Configs principais | Como funciona |
|---|---|---|
| **PIX** (Mercado Pago) | `DONATION_MP_PIX`, `DONATION_MP_TOKEN`, `DONATION_MP_PIX_PRICE`, `DONATION_MP_PIX_EXPIRATION_TIME`, `DONATION_MP_PIX_DROPDOWN[]`, `DONATION_MP_PIX_ACCOUNT_OWNER/CPF/BANK` | QR Code dinâmico EMV/BR Code gerado por `DDSConverter` (lib `com.google.zxing`), pago via app do banco. |
| **Mercado Pago Link** | `DONATION_MP_LINK`, `DONATION_MP_TOKEN`, `DONATION_MP_LINK_PRICE`, `DONATION_MP_CURRENCY`, `DONATION_MP_LINK_DROPDOWN[]` | Checkout web MP, suporta múltiplas moedas (`DONATION_MP_CURRENCIES[]`). |
| **PayPal** | `DONATION_PAYPAL_LINK`, `_CLIENT_ID`, `_CLIENT_SECRET`, `_SANDBOX_ENABLED`, `_CURRENCY`, `_DROPDOWN[]`, `_ACCOUNT_EMAIL` | Invoice API; sandbox opcional para testes. |
| **Binance (Crypto)** | `DONATION_BINANCE_PAY`, `_API_KEY`, `_SECRET_KEY`, `_PRICE`, `_FIAT_CURRENCY`, `_PAY_CURRENCY[]`, `_DROPDOWN[]` | Recebe pagamento em crypto (BTC/ETH/USDT etc.) convertido para fiat via cotação da `CurrencyManager`. |

### Sistema de Cotação de Moedas (`CurrencyManager`)

- **Fiat** (`FiatCurrencyTask`, `DONATION_CURRENCY_TASK_INTERVAL` ms): AwesomeAPI (`DONATION_CURRENCY_AWESOMEAPI=true`) ou CoinBase (`DONATION_CURRENCY_CB_API_KEY`).
- **Crypto** (`CryptoCurrencyTask`, `DONATION_BINANCE_CURRENCY_TASK_INTERVAL` ms): Binance public ticker quando `DONATION_BINANCE_PAY=true`.
- Conversões cacheadas em `Map<String, BigDecimal>` (chave = `CRYPTO + FIAT`).
- `isCryptoAvailable(String)`, `getCryptoCurrencies()`, `convertFiat(...)` e `convertCrypto(...)` são as APIs públicas usadas por `DonationManager` para calcular o preço exibido ao jogador.

### Status de Compra (`PurchaseStatus`)

```
CREATED  → PENDING (link/QR gerado) → WAITING (aguardando pagamento)
                                          ↓
                                       PAID  → entrega de item
                                       CANCELLED (timeout DONATION_PAY_TIME / DONATION_CHECK_TIME)
                                       EXPIRED
```

`DONATION_HIDE_ENDED=true` esconde compras finalizadas da listagem do jogador.

### Envio de E-mail

- Provedor: **MailerSend** (`DONATION_MAILER_TOKEN`, `DONATION_MAILER_ADDRESS`, `DONATION_MAILER_TEMPLATE`).
- Limite por hora: `DONATION_MAXIMUM_NUMBER_EMAILS`.
- Templates HTML em `game/data/html/mail/`.
- `Mailersend.sendAsync(...)` é non-blocking (roda em `ThreadPool`).
- Filtro de e-mails permitidos: `DONATION_ALLOWED_EMAILS[]` (vazio = aceita todos).

### Anti-Flood / Segurança

- Bypass `donation` passa por `FloodProtector` (já existente no engine).
- `DONATION_REQUIRE_TERMS=true` exige aceite de termos (`table_terms.htm`) antes de prosseguir.
- Validação de e-mail e player online antes de gerar pagamento.
- `CurrencyManager` nunca confia em valor enviado pelo cliente — preço sempre é recalculado server-side.

### Configurações Completas (`donation.properties`)

Todas as ~60 chaves abaixo ficam em `game/config/donation.properties`:

| Categoria | Chaves |
|---|---|
| **Master** | `ENABLE_PIX_MOD`, `DONATION_ENABLED`, `DONATION_SERVER_NAME`, `DONATION_PAY_TIME`, `DONATION_CHECK_TIME` |
| **Item entregue** | `DONATION_PURCHASABLE_ITEM` (itemId), `ANNOUNCE_DONATOR_ITEM_GLOBAL` |
| **Comportamento de UI** | `DONATION_CALCULATOR`, `DONATION_DROPDOWN`, `DONATION_HIDE_ENDED`, `DONATION_DELETE_INACTIVE`, `DONATION_DELETE_PAYMENT_DATA`, `DONATION_REQUIRE_TERMS` |
| **PIX** | `DONATION_MP_PIX`, `_TOKEN`, `_PIX_PRICE`, `_PIX_EXPIRATION_TIME`, `_PIX_MAIL`, `_PIX_ACCOUNT_OWNER/CPF/BANK`, `_PIX_DROPDOWN[]` |
| **MP Link** | `DONATION_MP_LINK`, `_LINK_PRICE`, `_CURRENCY`, `_CURRENCIES[]`, `_LINK_EXPIRATION_TIME`, `_LINK_DROPDOWN[]`, `_LINK_MAIL` |
| **PayPal** | `DONATION_PAYPAL_LINK`, `_CLIENT_ID`, `_CLIENT_SECRET`, `_PRICE`, `_SANDBOX_ENABLED`, `_ACCOUNT_EMAIL`, `_MAIL`, `_CURRENCY`, `_CURRENCIES[]`, `_DROPDOWN[]`, `_LINK_EXPIRATION_TIME`, `_WEBSITE`, `_NOTE_MSG`, `_LOGO_IMAGE`, `_PHONE_CODE`, `_PHONE_NUMBER` |
| **Binance** | `DONATION_BINANCE_PAY`, `_API_KEY`, `_SECRET_KEY`, `_PRICE`, `_FIAT_CURRENCY`, `_PAY_CURRENCY[]`, `_DROPDOWN[]`, `_EXPIRATION_TIME`, `_MAIL` |
| **Cotação** | `DONATION_CURRENCY_CB_API_KEY`, `DONATION_CURRENCY_AWESOMEAPI`, `DONATION_CURRENCY_TASK_INTERVAL`, `DONATION_BINANCE_CURRENCY_TASK_INTERVAL` |
| **E-mail** | `DONATION_MAILER_TOKEN`, `_ADDRESS`, `_TEMPLATE`, `DONATION_MAXIMUM_NUMBER_EMAILS`, `DONATION_ALLOWED_EMAILS[]` |

### CountryLocaleManager (auto-detecção de idioma por IP)

Implementado em `java/ext/mods/gameserver/data/manager/CountryLocaleManager.java`. Quando o jogador entra no mundo (`EnterWorld`), o manager dispara uma consulta assíncrona (não bloqueia login) à API `ip-api.com`:

```
GET http://ip-api.com/json/<IP>?fields=status,country,countryCode
```

- Se o `countryCode` (ex: `"BR"`, `"US"`, `"RU"`) estiver no `CountryLocaleMap`, aplica `player.setLocale(Locale.forLanguageTag(...))`.
- IPs privados (10/8, 172.16/12, 192.168/16, 127/8, 169.254/16, `::1`, `fe80::/10`) são **ignorados** sem chamada HTTP.
- Cache de idem-potência por `objectId` impede re-aplicação em re-entradas.
- Timeout: `CountryLocaleTimeoutMs` (default 2500 ms).

Configuração em `game/config/language.properties`:

```properties
CountryLocaleEnable=true       # master switch
CountryLocaleNotify=true       # envia sysstring 12100-12102 ao jogador
CountryLocaleAutoSet=true      # aplica setLocale() (se false, só notifica)
CountryLocaleApiUrl=http://ip-api.com/json/%s?fields=status,country,countryCode
CountryLocaleTimeoutMs=2500
CountryLocaleMap=BR=pt-BR,US=en-US,RU=ru-RU,PT=pt-BR,GB=en-US,FR=en-US,DE=en-US,ES=en-US,IT=en-US
```

Sysstrings (em todos os locales `game/data/locale/<lang>/sysstring.xml`, chaves `12100..12102`):
- **12100**: "Seu idioma foi definido automaticamente como `{locale}` com base na sua localização ({country})."
- **12101**: "Idioma desconhecido para o país `{country}`. Mantendo `{locale}`."
- **12102**: "Não foi possível detectar sua localização. Mantendo `{locale}`."

### Locales Suportados

A pasta `game/data/locale/` contém três locales ativos (carregados em `Config.LOCALES`):

| Locale | Pasta no disco (Java `Locale.toString()`) | Config tag (BCP-47) |
|---|---|---|
| Inglês (EUA) | `en_US/` | `en-US` |
| Português (Brasil) | `pt_BR/` | `pt-BR` |
| Russo | `ru_RU/` | `ru-RU` |

**Importante**: o nome da pasta em disco segue `Locale.toString()` (com underscore), mas a entrada em `language.properties` (`locales=en-US,pt-BR,ru-RU`) usa BCP-47 (com hífen). O `Config` faz `Locale.forLanguageTag("pt-BR")` que retorna um `Locale` cujo `.toString()` é `pt_BR`, e o `AbstractLocaleData.resolve()` consulta a pasta correta.

### Eventos
- **Capture the Flag (CTF)**
- **Death Match (DM)**
- **Last Man Standing (LM)**
- **Team vs Team (TvT)**
- **Random Farm Events**
- **Battle Boss** — eventos de boss com countdown

### Infraestrutura
- **GeoEngine + PathFinder** — pathfinding avançado com JPS+, R-Tree, suavização Catmull-Rom
- **Coroutine Pool** — thread pool baseado em Kotlin Coroutines
- **JVM Optimizer** — otimizações automáticas para JDK 25 (AppCDS, Compact Headers, THP)
- **HWID Protection** — anti-multi-client com criptografia VMPC
- **License Validator** — sistema de licenciamento por IP/email/chave
- **DeepL Translator** — tradução automática de conteúdo em runtime
- **Extension Loader** — carregamento dinâmico de módulos (.ext.jar)
- **Email Delivery** — sistema de e-mail in-game com proteção de itens

---

## 🧭 Pathfinding e Movimentação

O sistema de pathfinding é uma das áreas mais avançadas do projeto, escrito em Kotlin:

- **AdvancedPathFinder** — JPS+ (Jump Point Search Plus) com R-Tree spatial index
- **SmoothObstacleAvoidance** — suavização de rotas com curvas Catmull-Rom
- **DynamicObstacleLayer** — obstáculos dinâmicos em runtime
- **PeaceZoneCollisionManager** — colisão de peace zones
- **CreatureMove/PlayerMove/NpcMove** — movimentação especializada por tipo de ator
- **PathfinderCache** — cache de caminhos para performance
- **L2BREngine** — motor proprietário de pathfinding

---

## 🔒 Segurança

- Sistema de HWID previne multi-client
- Criptografia VMPC para pacotes de rede
- Validação de licença por IP/email
- Proteção anti-cheat integrada

---

## 📊 Portas

| Porta | Serviço |
|-------|---------|
| 7777 | GameServer |
| 2106 | LoginServer |

---

## 📚 Documentação

A pasta `docs/` contém 28 documentos técnicos detalhados cobrindo:

- Sistema de movimentação e pathfinding
- Otimizações de performance (Coroutine Pool, JVM)
- Changelog de versões
- Fluxogramas de ataque e cast
- Sistema de colisão de criaturas
- Migração do PlayerAI
- AutoFarm (melhorias e rotas)

---

## 📄 Licença

GNU General Public License v3.0 — veja [LICENSE](LICENSE).

---

**Versão:** 2.9.8  
**Build:** 2026  
**Comunidade:** [L2JBrasil.com](https://l2jbrasil.com)

---
### 👨‍💻 Filosofia de Desenvolvimento (Dev ⩿ A.L.N/⪀)

> *"Eu testo e corrijo o projeto BrProject como desenvolvedor ativo da equipe. Minha abordagem é prática: identificar problemas reais durante os testes, documentar cada correção em primeira pessoa, e garantir que todas as funcionalidades sejam preservadas enquanto melhoramos a experiência do usuário."*

**Compromissos:**
- ✅ **Teste real** - Uso o projeto como usuário final
- ✅ **Correção prática** - Soluções que funcionam no servidor real
- ✅ **Documentação completa** - Tudo documentado em `correcao/`
- ✅ **Assinatura pessoal** - Todas as correções com `Dev ⩿ A.L.N/⪀`

**Última atualização:** 09/07/2026  
**Repositório pessoal:** [ALN2025/Public_Brproject_DevALN](https://github.com/ALN2025/Public_Brproject_DevALN)
