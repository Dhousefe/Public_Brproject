' === LicenseInit Runner ===
Dim javaPath
Dim command
Dim exitcode
Set shell = WScript.CreateObject("WScript.Shell")

' --- CONFIGURAÇÃO MANUAL DO JAVA 25 ---
' Caminho direto para o executavel do Java 25
' Usamos aspas triplas ("") para garantir que o espaco em "Program Files" nao quebre o script
javaPath = """C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\java.exe"""

' === Comando principal ===
' libs/* para pegar todos os .jar dentro de libs/
' Nota: Adicionei espaco antes de -Xmx
command = javaPath & " -Xmx512m -Dsun.java2d.opengl=false -Dsun.java2d.d3d=false -Dsun.java2d.pmoffscreen=false -Dbrproject.safe.graphics=true -cp libs/*; ext.mods.security.LicenseInit"

' === Loop de execução ===
exitcode = 0
Do
    ' Executa o comando e mantém o console fechado (0 = oculto, 1 = mostra)
    ' A estrutura cmd /c requer aspas ao redor de tudo se o comando interno tiver aspas, 
    ' mas neste caso simples, concatenar deve funcionar.
    exitcode = shell.Run("cmd /c " & command & " & exit", 0, True)

    ' Trata o código de saída
    If exitcode = 2 Then
        ' Reinicia
        exitcode = 2
    ElseIf exitcode <> 0 Then
        ' Qualquer outro erro encerra o loop
        exitcode = 0
        Exit Do
    End If
Loop While exitcode = 2