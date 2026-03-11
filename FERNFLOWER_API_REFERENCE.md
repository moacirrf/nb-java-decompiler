# Guia Completo da API Fernflower do JetBrains

## Visão Geral

Fernflower é o decompilador Java oficial do JetBrains, mantido como parte do projeto IntelliJ Community. Este documento fornece uma referência completa da API para decompilação de classes Java.

## Fonte Oficial

- **Repositório**: https://github.com/JetBrains/intellij-community/tree/master/plugins/java-decompiler
- **Documentação**: https://github.com/JetBrains/intellij-community/blob/master/plugins/java-decompiler/engine/README.md
- **Licença**: Apache 2.0

---

## 1. Arquitetura Geral

### Classes Principais

```
org.jetbrains.java.decompiler.main
├── Fernflower (classe principal do motor de decompilação)
└── decompiler
    └── BaseDecompiler (fachada pública para usar Fernflower)
```

### Interfaces Principais

```
org.jetbrains.java.decompiler.main.extern
├── IBytecodeProvider    (fornecer bytecode das classes)
├── IResultSaver         (salvar resultado da decompilação)
├── IFernflowerLogger    (loggar eventos de decompilação)
└── IMemberIdentifierRenamer (opcional: renomear identificadores)
```

---

## 2. Interface: IBytecodeProvider

### Propósito
Responsável por fornecer o bytecode das classes Java que serão decompiladas.

### Assinatura da Interface

```java
public interface IBytecodeProvider {
    byte[] getBytecode(String externalPath, String internalPath) throws IOException;
}
```

### Parâmetros

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `externalPath` | String | Caminho externo do arquivo (ex: "com/example/MyClass.class") |
| `internalPath` | String | Caminho interno para resolução (geralmente o mesmo que externalPath) |

### Retorno
- `byte[]` - Array de bytes contendo o bytecode compilado da classe
- `null` ou lançar `IOException` se o arquivo não for encontrado

### Exemplo de Implementação

```java
public class FileSystemBytecodeProvider implements IBytecodeProvider {
    private final Path sourcePath;

    public FileSystemBytecodeProvider(Path sourcePath) {
        this.sourcePath = sourcePath;
    }

    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        // Normalizar path
        String normalizedPath = internalPath.replace('/', File.separatorChar);
        Path classFile = sourcePath.resolve(normalizedPath);
        
        if (!Files.exists(classFile)) {
            return null; // ou lançar IOException
        }
        
        return Files.readAllBytes(classFile);
    }
}
```

### Caso de Uso: NetBeans FileSystem

```java
public class NetbeansBytecodeProviderImpl implements IBytecodeProvider {
    private final FileSystemHelper helper;

    public NetbeansBytecodeProviderImpl(FileSystemHelper helper) {
        this.helper = helper;
    }

    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        // Usar FileSystemHelper do NetBeans para resolver recursos
        FileObject fileObj = helper.findResource(internalPath);
        if (fileObj == null) {
            return null;
        }
        return fileObj.asBytes();
    }
}
```

---

## 3. Interface: IResultSaver

### Propósito
Responsável por manipular os resultados da decompilação (salvar código fonte).

### Assinatura Completa da Interface

```java
public interface IResultSaver {
    // Criar estruturas de diretório
    void saveFolder(String path);
    void saveDirEntry(String path, String archiveName, String entryName);
    
    // Salvar arquivos de classe decompilados
    void saveClassFile(String path, String qualifiedName, String entryName, 
                       String content, int[] mapping);
    void saveClassFile(String path, String qualifiedName, String entryName, 
                       String content);
    void saveClassEntry(String path, String archiveName, String qualifiedName, 
                        String entryName, String content);
    
    // Manipular arquivos normais
    void copyFile(String source, String path, String entryName);
    
    // Gerenciar arquivos de arquivo (JAR/ZIP)
    void createArchive(String path, String archiveName, Manifest manifest);
    void copyEntry(String source, String path, String archiveName, String entry);
    void closeArchive(String path, String archiveName);
}
```

### Métodos Principais

#### saveClassFile (para decompilação de classe única)
```java
/**
 * @param path          Caminho de destino base
 * @param qualifiedName Nome completamente qualificado da classe (ex: "com.example.MyClass")
 * @param entryName     Nome da entrada (geralmente igual ao qualifiedName com .java)
 * @param content       Código fonte decompilado
 * @param mapping       Array de mapeamento de linha (pode ser null)
 */
void saveClassFile(String path, String qualifiedName, String entryName, 
                   String content, int[] mapping);
```

### Exemplo de Implementação para Captura de String

```java
public class StringResultSaver implements IResultSaver {
    private final StringBuilder resultBuffer = new StringBuilder();

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, 
                              String content, int[] mapping) {
        resultBuffer.append(content);
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, 
                              String content) {
        resultBuffer.append(content);
    }

    @Override
    public void saveClassEntry(String path, String archiveName, String qualifiedName, 
                               String entryName, String content) {
        resultBuffer.append(content);
    }

    // Outros métodos: implementar vazio ou com lógica apropriada
    @Override
    public void saveFolder(String path) {}
    
    @Override
    public void saveDirEntry(String path, String archiveName, String entryName) {}
    
    @Override
    public void copyFile(String source, String path, String entryName) {}
    
    @Override
    public void copyEntry(String source, String path, String archiveName, String entry) {}
    
    @Override
    public void createArchive(String path, String archiveName, Manifest manifest) {}
    
    @Override
    public void closeArchive(String path, String archiveName) {}

    public String getResult() {
        return resultBuffer.toString();
    }
}
```

### Exemplo de Implementação para Sistema de Arquivos

```java
public class FileSystemResultSaver implements IResultSaver {
    private final Path outputPath;

    public FileSystemResultSaver(Path outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, 
                              String content, int[] mapping) {
        try {
            // Converter nome qualificado em caminho: "com.example.MyClass" -> "com/example/MyClass.java"
            String relativePath = qualifiedName.replace('.', File.separatorChar) + ".java";
            Path outputFile = outputPath.resolve(relativePath);
            
            // Criar diretórios se necessário
            Files.createDirectories(outputFile.getParent());
            
            // Salvar o código
            Files.write(outputFile, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar classe: " + qualifiedName, e);
        }
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, 
                              String content) {
        saveClassFile(path, qualifiedName, entryName, content, null);
    }

    @Override
    public void saveFolder(String path) {
        try {
            Path folderPath = outputPath.resolve(path);
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar diretório: " + path, e);
        }
    }

    // Implementar outros métodos conforme necessário...
    @Override
    public void saveDirEntry(String path, String archiveName, String entryName) {}
    @Override
    public void copyFile(String source, String path, String entryName) {}
    @Override
    public void copyEntry(String source, String path, String archiveName, String entry) {}
    @Override
    public void createArchive(String path, String archiveName, Manifest manifest) {}
    @Override
    public void closeArchive(String path, String archiveName) {}
}
```

---

## 4. Classe: IFernflowerLogger (abstrata)

### Propósito
Define callbacks para loggar eventos durante a decompilação.

### Métodos Principais

```java
public abstract class IFernflowerLogger {
    public enum Severity {
        TRACE("TRACE: "), 
        INFO("INFO:  "), 
        WARN("WARN:  "), 
        ERROR("ERROR: ");
        
        public final String prefix;
        Severity(String prefix) { this.prefix = prefix; }
    }

    // Métodos abstratos - DEVEM ser implementados
    public abstract void writeMessage(String message, Severity severity);
    public abstract void writeMessage(String message, Severity severity, Throwable t);
    
    // Métodos de callback (hook methods - opcionais)
    public void startReadingClass(String className) { }
    public void endReadingClass() { }
    public void startClass(String className) { }
    public void endClass() { }
    public void startMethod(String methodName) { }
    public void endMethod() { }
    public void startWriteClass(String className) { }
    public void endWriteClass() { }
    
    // Gerenciamento de severidade
    public boolean accepts(Severity severity);
    public void setSeverity(Severity severity);
}
```

### Exemplo de Implementação

```java
public class ConsoleLogger extends IFernflowerLogger {
    @Override
    public void writeMessage(String message, Severity severity) {
        if (accepts(severity)) {
            System.out.println(severity.prefix + message);
        }
    }

    @Override
    public void writeMessage(String message, Severity severity, Throwable t) {
        if (accepts(severity)) {
            System.out.println(severity.prefix + message);
            if (t != null) {
                t.printStackTrace();
            }
        }
    }

    @Override
    public void startReadingClass(String className) {
        writeMessage("Iniciando leitura de: " + className, Severity.TRACE);
    }

    @Override
    public void startWriteClass(String className) {
        writeMessage("Iniciando escrita de: " + className, Severity.TRACE);
    }
}
```

### Implementação com PrintStream (já disponível)

```java
// O Fernflower fornece PrintStreamLogger
IFernflowerLogger logger = new PrintStreamLogger(System.out);
logger.setSeverity(IFernflowerLogger.Severity.INFO);
```

---

## 5. Classe: BaseDecompiler

### Propósito
Fachada pública que encapsula a lógica de decompilação do Fernflower.

### Construtores

```java
public class BaseDecompiler {
    // Construtor com cancellationManager (permite cancelamento assíncrono)
    public BaseDecompiler(IBytecodeProvider provider, 
                         IResultSaver saver, 
                         @Nullable Map<String, Object> options, 
                         IFernflowerLogger logger,
                         @Nullable CancellationManager cancellationManager) { }

    // Construtor sem cancellationManager
    public BaseDecompiler(IBytecodeProvider provider, 
                         IResultSaver saver, 
                         @Nullable Map<String, Object> options, 
                         IFernflowerLogger logger) { }
}
```

### Métodos Principais

```java
public class BaseDecompiler {
    // Adicionar arquivo(s) a decompilação
    public void addSource(File source);
    
    // Adicionar biblioteca de referência (não será decompilada)
    public void addLibrary(File library);
    
    // Executar a decompilação
    public void decompileContext();
}
```

### Opções de Decompilação

```java
Map<String, Object> options = new HashMap<>();

// Opções mais comuns:
options.put("rbr", "1");    // Hide bridge methods (1=true, 0=false)
options.put("rsy", "1");    // Hide synthetic class members
options.put("din", "1");    // Decompile inner classes
options.put("dc4", "1");    // Collapse 1.4 class references
options.put("das", "1");    // Decompile assertions
options.put("hes", "1");    // Hide empty super invocation
options.put("hdc", "1");    // Hide empty default constructor
options.put("dgs", "1");    // Decompile generic signatures
options.put("ner", "1");    // Assume return not throwing exceptions
options.put("den", "1");    // Decompile enumerations
options.put("rgn", "1");    // Remove getClass() invocation
options.put("rer", "1");    // Remove empty exception ranges
options.put("udv", "1");    // Reconstruct variable names from debug info
options.put("ump", "1");    // Reconstruct parameter names
options.put("inn", "1");    // Check for @NotNull annotations
options.put("lac", "0");    // Decompile lambda to anonymous classes
options.put("nls", "1");    // New line format (0=\r\n, 1=\n)
options.put("ind", "    "); // Indentation (default: 3 spaces)
options.put("log", "INFO"); // Log level: TRACE, INFO, WARN, ERROR
options.put("mpm", "0");    // Max processing time per method (0=no limit)
options.put("ren", "0");    // Rename obfuscated identifiers

// Exemplos de valores esperados por tipo:
// - String: "true", "false", número como string, ou valor textual
// - Opções booleanas: "1" (ativo) ou "0" (inativo)
```

---

## 6. Fluxo Completo de Decompilação

### Exemplo Básico: Decompilação de Classe Única

```java
import org.jetbrains.java.decompiler.main.decompiler.BaseDecompiler;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SimpleDecompilerExample {
    
    public static String decompileSingleClass(File classFile) throws Exception {
        // 1. Configurar opções
        Map<String, Object> options = new HashMap<>();
        options.put("rbr", "1");
        options.put("rsy", "1");
        options.put("din", "1");
        options.put("udv", "1");
        options.put("ump", "1");
        
        // 2. Criar logger
        IFernflowerLogger logger = new PrintStreamLogger(System.out);
        
        // 3. Criar provider de bytecode (carrega do arquivo)
        IBytecodeProvider provider = (externalPath, internalPath) -> {
            try {
                return Files.readAllBytes(Paths.get(externalPath));
            } catch (Exception e) {
                return null;
            }
        };
        
        // 4. Criar result saver (captura resultado em string)
        StringResultSaver saver = new StringResultSaver();
        
        // 5. Criar decompilador
        BaseDecompiler decompiler = new BaseDecompiler(provider, saver, options, logger);
        
        // 6. Adicionar arquivo a decompilação
        // IMPORTANTE: o caminho deve ser o caminho absoluto do arquivo .class
        decompiler.addSource(classFile);
        
        // 7. Executar decompilação
        decompiler.decompileContext();
        
        // 8. Obter resultado
        return saver.getResult();
    }
    
    public static void main(String[] args) throws Exception {
        File classFile = new File("target/classes/com/example/MyClass.class");
        String decompiled = decompileSingleClass(classFile);
        System.out.println(decompiled);
    }
}

public class StringResultSaver implements IResultSaver {
    private final StringBuilder buffer = new StringBuilder();

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, 
                              String content, int[] mapping) {
        buffer.append(content);
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, 
                              String content) {
        buffer.append(content);
    }

    @Override
    public void saveClassEntry(String path, String archiveName, String qualifiedName, 
                               String entryName, String content) {
        buffer.append(content);
    }

    // Implementar otros métodos como no-op
    @Override public void saveFolder(String path) {}
    @Override public void saveDirEntry(String path, String archiveName, String entryName) {}
    @Override public void copyFile(String source, String path, String entryName) {}
    @Override public void copyEntry(String source, String path, String archiveName, String entry) {}
    @Override public void createArchive(String path, String archiveName, Manifest manifest) {}
    @Override public void closeArchive(String path, String archiveName) {}

    public String getResult() {
        return buffer.toString();
    }
}
```

### Exemplo Avançado: Decompilação de Múltiplas Classes

```java
public class AdvancedDecompilerExample {
    
    public static void decompileJarFile(File jarFile, File outputDir) throws Exception {
        // 1. Configurar opções
        Map<String, Object> options = new HashMap<>();
        options.put("rbr", "1");
        options.put("rsy", "1");
        options.put("din", "1");
        options.put("udv", "1");
        options.put("ump", "1");
        options.put("log", "INFO");
        
        // 2. Logger
        IFernflowerLogger logger = new PrintStreamLogger(System.out);
        
        // 3. Provider: carrega bytecode do JAR ou diretório
        IBytecodeProvider provider = createZipAwareBytecodeProvider(jarFile);
        
        // 4. Saver: salva em arquivos
        IResultSaver saver = new FileSystemResultSaver(outputDir);
        
        // 5. Criar decompilador
        BaseDecompiler decompiler = new BaseDecompiler(provider, saver, options, logger);
        
        // 6. Adicionar fonte
        decompiler.addSource(jarFile);
        
        // 7. Executar
        decompiler.decompileContext();
        
        System.out.println("Decompilação concluída em: " + outputDir.getAbsolutePath());
    }
    
    private static IBytecodeProvider createZipAwareBytecodeProvider(File source) {
        return (externalPath, internalPath) -> {
            try {
                if (source.isDirectory()) {
                    // Arquivo do sistema
                    return Files.readAllBytes(Paths.get(externalPath));
                } else if (source.getName().endsWith(".jar") || 
                           source.getName().endsWith(".zip")) {
                    // Arquivo JAR/ZIP
                    try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(source)) {
                        java.util.zip.ZipEntry entry = zf.getEntry(internalPath);
                        if (entry != null) {
                            return java.nio.file.Files.readAllBytes(
                                Paths.get(externalPath, internalPath));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };
    }
}
```

---

## 7. Opções de Decompilação Detalhadas

### Opções Booleanas (1=ativo, 0=inativo)

| Opção | Default | Descrição |
|-------|---------|-----------|
| `rbr` | 1 | Hide bridge methods |
| `rsy` | 0 | Hide synthetic class members |
| `din` | 1 | Decompile inner classes |
| `dc4` | 1 | Collapse 1.4 class references |
| `das` | 1 | Decompile assertions |
| `hes` | 1 | Hide empty super invocation |
| `hdc` | 1 | Hide empty default constructor |
| `dgs` | 0 | Decompile generic signatures |
| `ner` | 1 | Assume return not throwing exceptions |
| `den` | 1 | Decompile enumerations |
| `rgn` | 1 | Remove getClass() invocation |
| `lit` | 0 | Output numeric literals as-is |
| `asc` | 0 | Encode non-ASCII as Unicode escapes |
| `bto` | 1 | Interpret int 1 as boolean true |
| `nns` | 0 | Allow not set synthetic attribute |
| `uto` | 1 | Nameless types as java.lang.Object |
| `udv` | 1 | Reconstruct variable names from debug info |
| `ump` | 1 | Reconstruct parameter names |
| `rer` | 1 | Remove empty exception ranges |
| `fdi` | 1 | De-inline finally structures |
| `inn` | 1 | Check for @NotNull annotation |
| `lac` | 0 | Decompile lambda to anonymous classes |
| `isl` | 1 | Inline simple lambda expressions |
| `ucrc` | 1 | Hide unnecessary record constructor |
| `cci` | 1 | Check AutoCloseable for try-with-resources |
| `jvn` | 0 | Use JAD style variable names |
| `jpr` | 0 | Include parameter names in JAD naming |
| `pbo` | 0 | Add parentheses for mixed bitwise operators |
| `crp` | 0 | Use record patterns where possible |
| `cps` | 0 | Use switch with patterns where possible |
| `iec` | 0 | Include entire classpath in context |

### Opções de String

| Opção | Default | Descrição |
|-------|---------|-----------|
| `nls` | OS-dependent | New line: 0=`\r\n` (Windows), 1=`\n` (Unix) |
| `ind` | 3 spaces | Indentation string |
| `log` | INFO | Log level: TRACE, INFO, WARN, ERROR |
| `mpm` | 0 | Max processing time per method (segundos, 0=sem limite) |
| `urc` | - | Classe customizada IMemberIdentifierRenamer para renaming |

---

## 8. Tratamento de Erros e Exceções

### Possíveis Exceções

```java
try {
    decompiler.decompileContext();
} catch (RuntimeException e) {
    // Fernflower pode lançar RuntimeException em várias situações
    logger.writeMessage("Erro durante decompilação: " + e.getMessage(), 
                       IFernflowerLogger.Severity.ERROR, e);
} finally {
    // Sempre limpar recursos se necessário
}
```

### Casos Comuns de Erro

| Situação | Possível Causa | Solução |
|----------|----------------|---------|
| ClassNotFoundException | Classe não encontrada | Verificar IBytecodeProvider |
| IOException ao ler bytecode | Arquivo corrompido | Validar arquivo .class |
| Decompilação incompleta | Timeout por método complexo | Aumentar `mpm` ou revisar opções |
| Java 8+ features não são decompilados corretamente | Bytecode inválido | Verificar versão do JDK |

---

## 9. Resumo da API Mínima Necessária

Para decompilação básica de uma classe, você precisa de:

```java
// 1. IBytecodeProvider - obrigatório
IBytecodeProvider provider = (extPath, intPath) -> {
    // Retornar byte[] do arquivo .class
};

// 2. IResultSaver - obrigatório
IResultSaver saver = new StringResultSaver(); // ou outro

// 3. IFernflowerLogger - obrigatório
IFernflowerLogger logger = new PrintStreamLogger(System.out);

// 4. Opções - opcional (pode ser null ou HashMap vazio)
Map<String, Object> options = new HashMap<>();
options.put("rbr", "1");

// 5. Criar e usar
BaseDecompiler decompiler = new BaseDecompiler(provider, saver, options, logger);
decompiler.addSource(new File("MyClass.class"));
decompiler.decompileContext();
```

---

## 10. Referências e Recursos

- **Repositório oficial**: https://github.com/JetBrains/intellij-community/tree/master/plugins/java-decompiler
- **Issue tracker**: https://youtrack.jetbrains.com/issues?q=project:%20IDEA%20subsystem:%20%22Java.%20Decompiler.%20Engine%22
- **ForgeFlower**: Reimplementação open-source mantida pelo projeto Minecraft Forge
- **Licença**: Apache License 2.0

---

## Notas Importantes

1. **Versão do Fernflower**: Este projeto usa `master-SNAPSHOT` via JitPack, que é a versão mais recente do repositório
2. **Compatibilidade**: Fernflower suporta Java desde versões antigas até Java 21+
3. **Performance**: Para múltiplas classes, considere usar threading/streams
4. **Segurança**: O Fernflower não valida código malicioso, use com cuidado em ambientes untrusted
5. **Debugging**: Use `IFernflowerLogger.Severity.TRACE` para informações detalhadas
