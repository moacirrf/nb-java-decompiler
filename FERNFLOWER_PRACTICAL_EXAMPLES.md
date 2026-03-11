# Exemplos Práticos: Usando Fernflower no Plugin NetBeans

Este documento contém exemplos práticos de como usar a API Fernflower, especialmente adaptado para o contexto do seu projeto nb-java-decompiler.

## 1. Estrutura Básica de Classes

### Seu Padrão Atual

Analisando seu código, você tem a seguinte estrutura:

```
DecompilerClassImpl
  ├── Implementa: Decompiler<String, FileObject>
  ├── Usa: BaseDecompiler + options
  └── Retorna: String com código decompilado

NetbeansBytecodeProviderImpl
  ├── Implementa: IBytecodeProvider
  ├── Recebe: FileSystemHelper (NetBeans utility)
  └── Carrega: bytecode via FileSystem do NetBeans

StringResultSaver
  ├── Implementa: IResultSaver
  ├── Funcionalidade: Captura resultado em StringBuilder
  └── Retorna: String via getResult()
```

---

## 2. Exemplo Completo: Decompilação de Classe do NetBeans

### Versão Melhorada (Com Tratamento de Erro)

```java
package io.github.moacirrf.javadecompiler.fernflower;

import io.github.moacirrf.javadecompiler.Decompiler;
import io.github.moacirrf.javadecompiler.ExceptionHandler;
import io.github.moacirrf.javadecompiler.files.FileSystemHelper;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.java.decompiler.main.decompiler.BaseDecompiler;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.openide.filesystems.FileObject;

public class DecompilerClassImpl implements Decompiler<String, FileObject> {

    public static final String HEADER_COMMENT = 
        "// Source code recreated by Apache Netbeans (NB Java Decompiler)\n";

    private final Map<String, Object> options;

    public DecompilerClassImpl() {
        options = new HashMap<>();
        
        // Configurar opções de decompilação
        // ===================================
        
        // Sintaxe: opções booleanas usam "1" (ativo) ou "0" (inativo)
        options.put("rbr", "1");   // Hide bridge methods
        options.put("rsy", "1");   // Hide synthetic members
        options.put("din", "1");   // Decompile inner classes
        options.put("dc4", "1");   // Collapse 1.4 class references
        options.put("das", "1");   // Decompile assertions
        options.put("hes", "1");   // Hide empty super invocation
        options.put("hdc", "1");   // Hide empty default constructor
        options.put("dgs", "1");   // Decompile generic signatures
        options.put("rer", "1");   // Remove empty exception ranges
        options.put("den", "1");   // Decompile enumerations
        options.put("udv", "1");   // Reconstruct variable names from debug info
        options.put("ump", "1");   // Reconstruct parameter names
        options.put("ner", "1");   // Assume returns don't throw exceptions
        
        // Indentação e formatação
        options.put("ind", "    "); // 4 espaços de indentação
        options.put("nls", "1");    // Use \n para novas linhas (Unix-style)
        
        // Logging
        options.put("log", "WARN"); // Información de logging: TRACE, INFO, WARN, ERROR
    }

    @Override
    public String decompile(FileObject file) {
        // Usar try-catch wrapper para tratamento de exceções customizado
        return ExceptionHandler.wrap(ExceptionHandler::handleException)
            .get(() -> decompileInternal(file));
    }

    private String decompileInternal(FileObject file) throws Exception {
        // 1. Extrair nome da classe
        String className = FileSystemHelper.extractName(file);
        
        // 2. Criar helper para acessar filesystem
        FileSystemHelper helper = FileSystemHelper.of(file);
        
        // 3. Criar logger (envia mensagens para stderr)
        IFernflowerLogger logger = new PrintStreamLogger(System.err);
        
        // 4. Criar provider de bytecode
        //    Este é responsável por carregar o bytecode das classes
        IBytecodeProvider provider = new NetbeansBytecodeProviderImpl(helper);
        
        // 5. Criar result saver
        //    Captura o código decompilado em memória
        StringResultSaver saver = new StringResultSaver();
        
        // 6. Criar decompilador
        BaseDecompiler decompiler = new BaseDecompiler(provider, saver, options, logger);
        
        // 7. Adicionar arquivo para decompilar
        //    IMPORTANTE: deve ser um arquivo válido (File existente)
        java.io.File classFile = new java.io.File(className);
        decompiler.addSource(classFile);
        
        // 8. Executar decompilação
        //    Este é o método que faz todo o trabalho
        decompiler.decompileContext();
        
        // 9. Obter resultado
        String result = saver.getResult();
        
        // 10. Adicionar comentário de cabeçalho
        return HEADER_COMMENT + (result != null ? result : "");
    }
}
```

---

## 3. Implementação Detalhada: IBytecodeProvider

### Explicação do Conceito

O `IBytecodeProvider` é um **Strategy Pattern** que permite que você implemente a forma como o Fernflower obtém os arquivos `.class` para decompilação.

```
Fer decompiler
    │
    ├─ precisa do bytecode de: "com.example.MyClass"
    │
    └─ chama: provider.getBytecode("com/example/MyClass.class", "com/example/MyClass.class")
               │
               └─ SUA IMPLEMENTAÇÃO
                   │
                   ├─ Opção 1: Buscar da filesystem
                   ├─ Opção 2: Buscar de um JAR
                   ├─ Opção 3: Buscar do NetBeans FileSystem
                   └─ Opção 4: Buscar de banco de dados, rede, etc.
```

### Versão com Filesystem (Simples)

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;

public class SimpleFileSystemBytecodeProvider implements IBytecodeProvider {
    
    private final String baseDirectory;

    public SimpleFileSystemBytecodeProvider(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        // externalPath: "com/example/MyClass.class"
        // internalPath: "com/example/MyClass.class"
        
        String fullPath = baseDirectory + java.io.File.separator + internalPath;
        
        try {
            return Files.readAllBytes(Paths.get(fullPath));
        } catch (java.nio.file.NoSuchFileException e) {
            System.err.println("Classe não encontrada: " + fullPath);
            return null;
        }
    }
}
```

### Versão com JAR (Intermediária)

```java
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;

public class JarBytecodeProvider implements IBytecodeProvider {
    
    private final String jarPath;

    public JarBytecodeProvider(String jarPath) {
        this.jarPath = jarPath;
    }

    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        // internalPath: "com/example/MyClass.class"
        
        try (ZipFile jar = new ZipFile(jarPath)) {
            ZipEntry entry = jar.getEntry(internalPath);
            
            if (entry == null) {
                return null; // Classe não encontrada no JAR
            }
            
            // Ler o conteúdo da entrada
            byte[] buffer = new byte[(int) entry.getSize()];
            try (java.io.InputStream is = jar.getInputStream(entry)) {
                is.read(buffer);
            }
            
            return buffer;
        }
    }
}
```

### Versão NetBeans (Atual)

```java
public class NetbeansBytecodeProviderImpl implements IBytecodeProvider {

    private final FileSystemHelper helper;

    public NetbeansBytecodeProviderImpl(FileSystemHelper helper) {
        this.helper = helper;
    }

    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        // Converter caminho do class: "com.example.MyClass" -> "com/example/MyClass.class"
        String resourcePath = internalPath
            .replace('.', '/')
            .replace(".class", "")
            + ".class";
        
        // Buscar no FileSystem do NetBeans
        FileObject fileObj = helper.findResource(resourcePath);
        
        if (fileObj == null || !fileObj.isValid()) {
            return null;
        }
        
        // Converter FileObject do NetBeans para byte array
        return fileObj.asBytes();
    }
}
```

### Versão Avançada: Multi-font (Filesystem + JAR)

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;

public class MultiSourceBytecodeProvider implements IBytecodeProvider {
    
    private final java.util.List<String> searchPaths; // Diretórios
    private final java.util.List<String> jarPaths;    // JARs

    public MultiSourceBytecodeProvider() {
        this.searchPaths = new java.util.ArrayList<>();
        this.jarPaths = new java.util.ArrayList<>();
    }

    public void addSearchPath(String path) {
        searchPaths.add(path);
    }

    public void addJarPath(String jarPath) {
        jarPaths.add(jarPath);
    }

    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        // 1. Procurar em diretórios primeiro
        for (String searchPath : searchPaths) {
            String fullPath = searchPath + java.io.File.separator + internalPath;
            try {
                return Files.readAllBytes(Paths.get(fullPath));
            } catch (Exception e) {
                // Não encontrado, continuar
            }
        }
        
        // 2. Procurar em JARs
        for (String jarPath : jarPaths) {
            try (ZipFile jar = new ZipFile(jarPath)) {
                ZipEntry entry = jar.getEntry(internalPath);
                if (entry != null) {
                    byte[] buffer = new byte[(int) entry.getSize()];
                    try (java.io.InputStream is = jar.getInputStream(entry)) {
                        is.read(buffer);
                    }
                    return buffer;
                }
            } catch (Exception e) {
                // JAR inválido, continuar
            }
        }
        
        return null; // Não encontrado em lugar nenhum
    }
}
```

---

## 4. Implementação Detalhada: IResultSaver

### Padrão: Captura em String

```java
import java.util.jar.Manifest;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

public class StringResultSaver implements IResultSaver {

    private final StringBuilder resultBuffer = new StringBuilder();

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, 
                              String content, int[] mapping) {
        // IMPORTANTE: Este é o método chamado para output de classe individual
        resultBuffer.append(content);
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, 
                              String content) {
        // Variante sem mapping (usado pela maioria dos casos)
        resultBuffer.append(content);
    }

    @Override
    public void saveClassEntry(String path, String archiveName, String qualifiedName, 
                               String entryName, String content) {
        // Usado quando salvando dentro de um JAR/ZIP
        resultBuffer.append(content);
    }

    // Métodos não usados para decompilação simples
    @Override public void saveFolder(String path) {}
    @Override public void saveDirEntry(String path, String archiveName, String entryName) {}
    @Override public void copyFile(String source, String path, String entryName) {}
    @Override public void copyEntry(String source, String path, String archiveName, String entry) {}
    @Override public void createArchive(String path, String archiveName, Manifest manifest) {}
    @Override public void closeArchive(String path, String archiveName) {}

    public String getResult() {
        return resultBuffer.toString();
    }
}
```

### Padrão: Salvar em Filesystem

```java
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.Manifest;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

public class FileSystemResultSaver implements IResultSaver {

    private final Path outputPath;

    public FileSystemResultSaver(Path outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, 
                              String content, int[] mapping) {
        saveClassFile(path, qualifiedName, entryName, content);
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, 
                              String content) {
        try {
            // Converter: "com.example.MyClass" -> "com/example/MyClass.java"
            String relativePath = qualifiedName.replace('.', java.io.File.separatorChar) + ".java";
            Path outputFile = outputPath.resolve(relativePath);
            
            // Criar diretórios pai se necessário
            Files.createDirectories(outputFile.getParent());
            
            // Escrever o arquivo
            Files.write(outputFile, content.getBytes(StandardCharsets.UTF_8));
            
            System.out.println("Decompilado: " + outputFile);
        } catch (IOException e) {
            System.err.println("Erro ao salvar: " + qualifiedName);
            e.printStackTrace();
        }
    }

    @Override
    public void saveClassEntry(String path, String archiveName, String qualifiedName, 
                               String entryName, String content) {
        saveClassFile(path, qualifiedName, entryName, content);
    }

    @Override public void saveFolder(String path) {}
    @Override public void saveDirEntry(String path, String archiveName, String entryName) {}
    @Override public void copyFile(String source, String path, String entryName) {}
    @Override public void copyEntry(String source, String path, String archiveName, String entry) {}
    @Override public void createArchive(String path, String archiveName, Manifest manifest) {}
    @Override public void closeArchive(String path, String archiveName) {}
}
```

### Padrão: Acumular Resultados

```java
import java.util.*;
import java.util.jar.Manifest;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

public class AccumulatorResultSaver implements IResultSaver {

    private final Map<String, String> results = new LinkedHashMap<>();
    private String currentArchive;

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, 
                              String content, int[] mapping) {
        saveClassFile(path, qualifiedName, entryName, content);
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, 
                              String content) {
        // Armazenar com chave = nome qualificado
        String key = currentArchive != null ? 
            currentArchive + "/" + qualifiedName : 
            qualifiedName;
        results.put(key, content);
    }

    @Override
    public void createArchive(String path, String archiveName, Manifest manifest) {
        currentArchive = archiveName;
    }

    @Override
    public void closeArchive(String path, String archiveName) {
        currentArchive = null;
    }

    // Retornar resultados
    public Map<String, String> getResults() {
        return new LinkedHashMap<>(results);
    }

    public String getResult(String qualifiedName) {
        return results.get(qualifiedName);
    }

    @Override public void saveFolder(String path) {}
    @Override public void saveDirEntry(String path, String archiveName, String entryName) {}
    @Override public void copyFile(String source, String path, String entryName) {}
    @Override public void copyEntry(String source, String path, String archiveName, String entry) {}
}
```

---

## 5. Uso Completo: Exemplo Integrado

### Cenário: Decompilação de um JAR Completo

```java
import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.java.decompiler.main.decompiler.BaseDecompiler;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

public class JarDecompilerExample {

    public static void main(String[] args) throws Exception {
        // 1. Especificar arquivo de entrada
        String jarFilePath = "target/my-library.jar";
        
        // 2. Especificar diretório de saída
        String outputDir = "decompiled_source";
        
        // 3. Decompilação
        decompileJar(jarFilePath, outputDir);
    }

    public static void decompileJar(String jarPath, String outputDir) throws Exception {
        System.out.println("=== Decompilando JAR ===");
        System.out.println("Entrada: " + jarPath);
        System.out.println("Saída: " + outputDir);
        System.out.println();
        
        // 1. Configurar opções
        Map<String, Object> options = createDefaultOptions();
        
        // 2. Criar logger
        IFernflowerLogger logger = new PrintStreamLogger(System.out);
        logger.setSeverity(IFernflowerLogger.Severity.INFO);
        
        // 3. Criar provider (carrega de JAR/filesystem)
        IBytecodeProvider provider = new JarBytecodeProvider(jarPath);
        
        // 4. Criar saver (salva em filesystem)
        IResultSaver saver = new FileSystemResultSaver(Paths.get(outputDir));
        
        // 5. Criar decompilador
        BaseDecompiler decompiler = new BaseDecompiler(provider, saver, options, logger);
        
        // 6. Adicionar JAR como fonte
        decompiler.addSource(new File(jarPath));
        
        // 7. Executar decompilação
        long startTime = System.currentTimeMillis();
        try {
            decompiler.decompileContext();
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("\n✓ Decompilação concluída em " + duration + "ms");
        } catch (Exception e) {
            System.err.println("✗ Erro durante decompilação:");
            e.printStackTrace();
        }
    }

    private static Map<String, Object> createDefaultOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("rbr", "1");    // Hide bridge methods
        options.put("rsy", "1");    // Hide synthetic
        options.put("din", "1");    // Decompile inner classes
        options.put("dc4", "1");    // Collapse 1.4 references
        options.put("das", "1");    // Decompile assertions
        options.put("hes", "1");    // Hide empty super
        options.put("hdc", "1");    // Hide empty default constructor
        options.put("dgs", "1");    // Decompile generics
        options.put("rer", "1");    // Remove empty exception ranges
        options.put("den", "1");    // Decompile enums
        options.put("udv", "1");    // Reconstruct var names
        options.put("ump", "1");    // Reconstruct param names
        options.put("ind", "    "); // 4 spaces indentation
        options.put("nls", "1");    // Unix-style line endings
        options.put("log", "INFO"); // Log level
        return options;
    }
}
```

---

## 6. Tratamento de Erros Customizado

### Wrapper Seguro

```java
public class RobustFernflowerDecompiler {

    public static String decompileSafely(File classFile) {
        try {
            return decompile(classFile);
        } catch (Exception e) {
            return formatErrorResponse(classFile, e);
        }
    }

    private static String decompile(File classFile) throws Exception {
        if (!classFile.exists()) {
            throw new IllegalArgumentException("Arquivo não existe: " + classFile);
        }
        
        if (!classFile.getName().endsWith(".class")) {
            throw new IllegalArgumentException("Não é um arquivo .class: " + classFile);
        }
        
        Map<String, Object> options = new HashMap<>();
        options.put("rbr", "1");
        
        IFernflowerLogger logger = new PrintStreamLogger(System.err);
        IBytecodeProvider provider = (ext, int_) -> java.nio.file.Files
            .readAllBytes(classFile.toPath());
        StringResultSaver saver = new StringResultSaver();
        
        BaseDecompiler decompiler = new BaseDecompiler(provider, saver, options, logger);
        decompiler.addSource(classFile);
        decompiler.decompileContext();
        
        return saver.getResult();
    }

    private static String formatErrorResponse(File file, Exception e) {
        return String.format(
            "// ERRO ao decompilador arquivo: %s\n" +
            "// Razão: %s\n" +
            "// Stack trace:\n%s",
            file.getAbsolutePath(),
            e.getMessage(),
            formatStackTrace(e)
        );
    }

    private static String formatStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("//   at ").append(element).append("\n");
        }
        return sb.toString();
    }
}
```

---

## 7. Performance e Otimizações

### Caching de Bytecode

```java
public class CachingBytecodeProvider implements IBytecodeProvider {
    private final IBytecodeProvider delegate;
    private final java.util.Map<String, byte[]> cache = new java.util.HashMap<>();

    public CachingBytecodeProvider(IBytecodeProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        String key = internalPath;
        
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        
        byte[] bytecode = delegate.getBytecode(externalPath, internalPath);
        
        if (bytecode != null) {
            cache.put(key, bytecode);
        }
        
        return bytecode;
    }

    public void clearCache() {
        cache.clear();
    }
}
```

### Decompilação em Background

```java
public class BackgroundDecompiler {
    
    public static void decompileAsync(File file, 
                                      java.util.function.Consumer<String> onSuccess,
                                      java.util.function.Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                String result = decompile(file);
                onSuccess.accept(result);
            } catch (Exception e) {
                onError.accept(e);
            }
        }).start();
    }

    private static String decompile(File file) throws Exception {
        // Implementar decompilação
        return "";
    }
}
```

---

## 8. Testes Unitários

### Teste Básico

```java
import org.junit.Test;
import static org.junit.Assert.*;

public class FernflowerDecompilerTest {

    @Test
    public void testDecompileSingleClass() throws Exception {
        // Arrange
        File classFile = new File("target/classes/com/example/SimpleClass.class");
        DecompilerClassImpl decompiler = new DecompilerClassImpl();
        
        // Act
        String result = decompiler.decompile(classFile);
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("class SimpleClass"));
        assertTrue(result.contains("// Source code recreated"));
    }

    @Test
    public void testBytecodeProviderFindsClass() throws Exception {
        // Arrange
        IBytecodeProvider provider = new FileSystemBytecodeProvider("target/classes");
        
        // Act
        byte[] bytecode = provider.getBytecode(
            "com/example/SimpleClass.class",
            "com/example/SimpleClass.class"
        );
        
        // Assert
        assertNotNull(bytecode);
        assertTrue(bytecode.length > 0);
        // Primeiro 4 bytes são CAFEBABE em Java class files
        assertTrue((bytecode[0] & 0xFF) == 0xCA);
        assertTrue((bytecode[1] & 0xFF) == 0xFE);
    }

    @Test
    public void testResultSaverCapturesOutput() throws Exception {
        // Arrange
        StringResultSaver saver = new StringResultSaver();
        String expectedCode = "public class Test { }";
        
        // Act
        saver.saveClassFile("", "Test", "Test.java", expectedCode, null);
        String result = saver.getResult();
        
        // Assert
        assertEquals(expectedCode, result);
    }
}
```

---

Esses exemplos cobrem a maioria dos cenários de uso do Fernflower para decompilação de classes Java. Adapte conforme necessário para seu projeto NetBeans!
