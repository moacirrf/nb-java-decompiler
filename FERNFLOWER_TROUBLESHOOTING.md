# Guia de Resolução de Problemas - Fernflower

## Problemas Comuns e Soluções

### 1. Erro: "Method does not override supertype method"

**Sintoma**: Compilação falha com erro de override incorreto em `IBytecodeProvider`.

**Causa**: Mudanças na assinatura da interface do Fernflower entre versões.

**Versão observada no projeto**: `master-SNAPSHOT` via JitPack (versão recente)

**Histórico de Mudanças**:
- Versão antiga: `byte[] getBytecode(String externalPath, String internalPath)`
- Versão recente: Pode ter mudado para apenas um parâmetro

**Solução Verificar**:
```java
// 1. Verificar a assinatura correta no seu ambiente
// No IDE, vá até IBytecodeProvider e veja a assinatura

// 2. Se a assinatura for diferente, corrigir:
@Override
public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
    // Ambos os parâmetros são use normalmente
}

// 3. Alternativamente, se apenas um parâmetro:
@Override
public byte[] getBytecode(String externalPath) throws IOException {
    // Usar apenas externalPath
}
```

---

### 2. Erro: "Method saveClassFile is not implemented"

**Sintoma**: Compilação falha porque `StringResultSaver` não implementa todos os métodos de `IResultSaver`.

**Causa**: Interface `IResultSaver` possui vários métodos que precisam de implementação.

**Solução**: Implementar com métodos vazios (no-op) se não forem usados:

```java
@Override
public void saveFolder(String path) {
    // Não implementado - decompilação simples não usa
}

@Override
public void saveDirEntry(String path, String archiveName, String entryName) {
    // Não implementado
}

@Override
public void copyFile(String source, String path, String entryName) {
    // Não implementado
}

@Override
public void copyEntry(String source, String path, String archiveName, String entry) {
    // Não implementado
}

@Override
public void createArchive(String path, String archiveName, Manifest manifest) {
    // Não implementado
}

@Override
public void closeArchive(String path, String archiveName) {
    // Não implementado
}
```

---

### 3. Erro: "Decompilation returned empty result"

**Sintoma**: `StringResultSaver.getResult()` retorna string vazia.

**Possíveis Causas**:
1. `IBytecodeProvider` não está fornecendo bytecode válido
2. `BaseDecompiler.addSource()` foi chamado com arquivo inválido
3. `BaseDecompiler.decompileContext()` falhou silenciosamente

**Solução**:

```java
private String decompileInternal(FileObject file) throws Exception {
    // ... código anterior ...
    
    // 1. Validar entrada
    if (file == null || !file.isValid()) {
        throw new IllegalArgumentException("FileObject inválido");
    }
    
    // 2. Criar arquivo temporário com bytecode válido
    java.io.File classFile = java.io.File.createTempFile("class_", ".class");
    
    try {
        // Copiar bytecode para arquivo temporário
        java.nio.file.Files.write(classFile.toPath(), file.asBytes());
        
        // ... resto do código ...
        
        decompiler.addSource(classFile);
        decompiler.decompileContext();
        
        // 3. Validar resultado
        String result = saver.getResult();
        if (result == null || result.isEmpty()) {
            logger.writeMessage("Aviso: Decompilação retornou resultado vazio para: " + 
                              file.getName(), 
                              IFernflowerLogger.Severity.WARN);
        }
        
        return result;
    } finally {
        // Limpar arquivo temporário
        classFile.delete();
    }
}
```

---

### 4. Erro: "CancellationManager not found"

**Sintoma**: Classe `CancellationManager` não existe em `BaseDecompiler`.

**Causa**: Versões antigas do Fernflower não têm gerenciamento de cancelamento.

**Solução**: Usar construtor sem `CancellationManager`:

```java
// ✗ Errado (pode não existir em versões antigas)
BaseDecompiler decompiler = new BaseDecompiler(
    provider, saver, options, logger, null  // CancellationManager
);

// ✓ Correto - usar o construtor sem CancellationManager
BaseDecompiler decompiler = new BaseDecompiler(
    provider, saver, options, logger
);
```

---

### 5. Erro: "ClassNotFoundException" durante decompilação

**Sintoma**: Fernflower não consegue encontrar classes dependentes.

**Causa**: Classes referenciadas não estão disponíveis no classpath.

**Solução**: Adicionar bibliotecas como "library" (não decompiladas):

```java
// Adicionar como source (será decompilado)
decompiler.addSource(myClassFile);

// Adicionar como library (apenas para referência, não decompilado)
decompiler.addLibrary(new File("path/to/rt.jar"));          // JDK
decompiler.addLibrary(new File("path/to/dependency.jar")); // Dependências

// Executar
decompiler.decompileContext();
```

---

### 6. Erro: "No classes found in source"

**Sintoma**: Nenhuma classe foi decompilada mesmo após chamar `decompileContext()`.

**Causa possíveis**:
1. Arquivo não é um `.class` válido
2. Path incorreto fornecido ao `addSource()`
3. `IBytecodeProvider` retorna `null` para o arquivo

**Solução de Debug**:

```java
private String decompileInternal(FileObject file) throws Exception {
    // ... código setup ...
    
    // Criar um provider com debug
    IBytecodeProvider debugProvider = (externalPath, internalPath) -> {
        System.out.println("Provider chamado:")
        System.out.println("  externalPath: " + externalPath);
        System.out.println("  internalPath: " + internalPath);
        
        try {
            byte[] bytecode = helper.findResource(internalPath).asBytes();
            System.out.println("  bytecode size: " + bytecode.length);
            return bytecode;
        } catch (Exception e) {
            System.out.println("  ERRO: " + e.getMessage());
            return null;
        }
    };
    
    // Usar o provider com debug
    BaseDecompiler decompiler = new BaseDecompiler(
        debugProvider, saver, options, logger
    );
    
    // ... resto do código ...
}
```

---

### 7. Erro: "Invalid class file format"

**Sintoma**: `ClassFormatException` ou similar durante decompilação.

**Causa**: Arquivo `.class` está corrompido ou em formato incompatível.

**Solução**:

```java
private String decompileInternal(FileObject file) throws Exception {
    // 1. Validar magic number (CAFEBABE)
    byte[] bytecode = file.asBytes();
    
    if (bytecode.length < 4) {
        throw new Exception("Arquivo muito pequeno para ser class válido");
    }
    
    int magic = ((bytecode[0] & 0xFF) << 24) | 
                ((bytecode[1] & 0xFF) << 16) |
                ((bytecode[2] & 0xFF) << 8) |
                (bytecode[3] & 0xFF);
    
    if (magic != 0xCAFEBABE) {
        throw new Exception("Arquivo não é class válido (magic: 0x" + 
                          Integer.toHexString(magic) + ")");
    }
    
    // 2. Validar versão (bytes 4-5 são minor, 6-7 são major)
    int majorVersion = ((bytecode[6] & 0xFF) << 8) | (bytecode[7] & 0xFF);
    System.out.println("Versão Java: " + majorVersion);
    
    // 3. Prosseguir com decompilação
    // ... rest of code ...
}
```

---

### 8. Aviso: "Synthetic method not decompiled correctly"

**Sintoma**: Métodos sintéticos aparecem no código decompilado.

**Solução**: Habilitar opção de ocultação:

```java
options.put("rsy", "1");  // Hide synthetic members
```

---

### 9. Aviso: "Generic type information lost"

**Sintoma**: Código usa tipos sem genéricos: `List` em vez de `List<String>`.

**Solução**: Ativar decompilação de generic signatures:

```java
options.put("dgs", "1");  // Decompile generic signatures
```

---

### 10. Performance: Decompilação muito lenta

**Sintoma**: Leva muito tempo para decompilação de uma classe.

**Causa possível**: Método muito complexo travando o decompilador.

**Solução de timeout**:

```java
// Limitar tempo de processamento por método (em segundos)
options.put("mpm", "10");  // Máximo 10 segundos por método

// Aumentar se necessário para métodos muito complexos
options.put("mpm", "30");
```

---

### 11. ClassLoader Issues com NetBeans

**Sintoma**: Não consegue acessar FileObject do NetBeans.

**Causa**: Contexto de ClassLoader diferente.

**Solução**:

```java
public class NetbeansBytecodeProviderImpl implements IBytecodeProvider {
    private final FileSystemHelper helper;

    public NetbeansBytecodeProviderImpl(FileSystemHelper helper) {
        this.helper = helper;
        // Armazenar ClassLoader do NetBeans
        this.netlClassLoader = NetbeansBytecodeProviderImpl.class.getClassLoader();
    }

    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        // Usar ClassLoader do NetBeans
        Thread.currentThread().setContextClassLoader(nbeansClassLoader);
        
        try {
            FileObject fileObj = helper.findResource(internalPath);
            if (fileObj != null) {
                return fileObj.asBytes();
            }
        } finally {
            // Restaurar ClassLoader anterior
            Thread.currentThread().setContextClassLoader(
                FileystemHelper.class.getClassLoader()
            );
        }
        
        return null;
    }
}
```

---

## Matriz de Compatibilidade de Versão Fernflower

| Versão | Fonte | JDK Mín | Status |
|--------|-------|---------|--------|
| `master-SNAPSHOT` | JitPack | 11+ | Atual/Recomendado |
| `1.0.0` | Maven Central | 8+ | Estável |
| Versão IntelliJ 2023.x | IntelliJ | 11+ | Compatível |

---

## Processo de Debug Passo-a-Passo

### 1. Verificar arquivo de entrada

```bash
# No terminal, verificar magic number
hexdump -C MyClass.class | head -1
# Deve mostrar: ca fe ba be (CAFEBABE)
```

### 2. Verificar bytecode em Java

```java
byte[] bytes = Files.readAllBytes(Paths.get("MyClass.class"));
System.out.println("Tamanho: " + bytes.length);
System.out.printf("Magic: 0x%02X%02X%02X%02X\n", 
    bytes[0], bytes[1], bytes[2], bytes[3]);
System.out.printf("Versão: %d.%d\n", 
    ((bytes[6] & 0xFF) << 8) | (bytes[7] & 0xFF),
    ((bytes[4] & 0xFF) << 8) | (bytes[5] & 0xFF));
```

### 3. Test minimal

```java
@Test
public void testFernflowerMinimal() throws Exception {
    // 1. Compilar uma classe simples
    String javaCode = "public class Test { public void foo() { } }";
    // Compilar para Test.class
    
    // 2. Tentar decompilação minimal
    IBytecodeProvider provider = (ext, int_) -> 
        Files.readAllBytes(Paths.get("Test.class"));
    
    StringResultSaver saver = new StringResultSaver();
    IFernflowerLogger logger = new PrintStreamLogger(System.out);
    
    BaseDecompiler decompiler = new BaseDecompiler(
        provider, saver, null, logger
    );
    decompiler.addSource(new File("Test.class"));
    decompiler.decompileContext();
    
    String result = saver.getResult();
    System.out.println(result);
    assertNotNull(result);
    assertFalse(result.isEmpty());
}
```

---

## Recursos para Más Informações

- **GitHub Issues**: https://github.com/JetBrains/intellij-community/issues?q=fernflower
- **YourTrack**: https://youtrack.jetbrains.com/issues?q=Subsystem:%20Java.%20Decompiler.%20Engine
- **ForgeFlower**: https://github.com/MinecraftForge/ForgeFlower (implementação alternativa)
- **Stack Overflow**: Tag `fernflower` para perguntas comunitárias

