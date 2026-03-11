# Fernflower API - Quick Reference (Cheat Sheet)

## Importações Essenciais

```java
import org.jetbrains.java.decompiler.main.decompiler.BaseDecompiler;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
```

---

## Fluxo Mínimo de 5 Passos

```java
// 1️⃣ Opções
Map<String, Object> opts = new HashMap<>();
opts.put("rbr", "1");
opts.put("udv", "1");

// 2️⃣ Logger
IFernflowerLogger logger = new PrintStreamLogger(System.out);

// 3️⃣ Provider (Get bytecode)
IBytecodeProvider provider = (ext, int_) -> 
    Files.readAllBytes(Paths.get(int_));

// 4️⃣ Saver (Handle result)
StringResultSaver saver = new StringResultSaver();

// 5️⃣ Decompile
BaseDecompiler decompiler = new BaseDecompiler(provider, saver, opts, logger);
decompiler.addSource(new File("MyClass.class"));
decompiler.decompileContext();

String result = saver.getResult();
```

---

## Opções Mais Usadas

| Opção | Valores | Descrição | Default |
|-------|---------|-----------|---------|
| `rbr` | 0/1 | Hide bridge methods | 1 |
| `rsy` | 0/1 | Hide synthetic members | 0 |
| `din` | 0/1 | Decompile inner classes | 1 |
| `udv` | 0/1 | Reconstruct var names | 1 |
| `ump` | 0/1 | Reconstruct param names | 1 |
| `dgs` | 0/1 | Decompile generics | 0 |
| `log` | TRACE/INFO/WARN/ERROR | Log level | INFO |
| `ind` | string | Indentation | "   " |

---

## Interfaces Principais

### IBytecodeProvider

```java
public interface IBytecodeProvider {
    byte[] getBytecode(String externalPath, String internalPath) throws IOException;
    // Retorna: byte array do classe, ou null se não encontrada
}
```

**Variações rápidas**:

```java
// Filesystem
IBytecodeProvider fs = (ext, int_) -> 
    Files.readAllBytes(Paths.get("classes", int_));

// JAR
IBytecodeProvider jar = (ext, int_) -> {
    try (ZipFile z = new ZipFile("app.jar")) {
        ZipEntry e = z.getEntry(int_);
        if (e != null) return IOUtils.readFully(z.getInputStream(e));
    }
    return null;
};

// Lambda simples
IBytecodeProvider lambda = (ext, int_) -> null;
```

### IResultSaver

```java
// Métodos críticos
void saveClassFile(String path, String qualifiedName, 
                   String entryName, String content, int[] mapping);
void saveClassFile(String path, String qualifiedName, 
                   String entryName, String content);

// Todos os 8 métodos
void saveFolder(String path);
void saveDirEntry(String path, String archiveName, String entryName);
void copyFile(String source, String path, String entryName);
void copyEntry(String source, String path, String archiveName, String entry);
void createArchive(String path, String archiveName, Manifest manifest);
void saveClassEntry(String path, String archiveName, String qualifiedName, 
                    String entryName, String content);
void closeArchive(String path, String archiveName);
```

**Implementação mínima**:

```java
public class MyResultSaver implements IResultSaver {
    private StringBuilder buffer = new StringBuilder();
    
    @Override
    public void saveClassFile(String p, String q, String e, String c, int[] m) {
        buffer.append(c);
    }
    
    @Override
    public void saveClassFile(String p, String q, String e, String c) {
        buffer.append(c);
    }
    
    // Todos os outros: deixar vazio
    @Override public void saveFolder(String p) {}
    @Override public void saveDirEntry(String p, String a, String e) {}
    @Override public void copyFile(String s, String p, String e) {}
    @Override public void copyEntry(String s, String p, String a, String e) {}
    @Override public void createArchive(String p, String a, Manifest m) {}
    @Override public void saveClassEntry(String p, String a, String q, String e, String c) {}
    @Override public void closeArchive(String p, String a) {}
    
    public String getResult() { return buffer.toString(); }
}
```

### IFernflowerLogger

```java
// Severity levels
enum Severity { TRACE, INFO, WARN, ERROR }

// Métodos abstratos
public abstract void writeMessage(String message, Severity severity);
public abstract void writeMessage(String message, Severity severity, Throwable t);

// Callbacks (opcionais)
public void startReadingClass(String className) { }
public void startClass(String className) { }
public void startWriteClass(String className) { }
public void startMethod(String methodName) { }
// ... e.g. endReadingClass(), endClass(), endMethod(), endWriteClass()
```

**Implementação simples**:

```java
class MyLogger extends IFernflowerLogger {
    @Override
    public void writeMessage(String msg, Severity sev) {
        System.out.println("[" + sev + "] " + msg);
    }
    
    @Override
    public void writeMessage(String msg, Severity sev, Throwable t) {
        writeMessage(msg, sev);
        if (t != null) t.printStackTrace();
    }
}
```

**Usar a implementação pré-feita**:
```java
IFernflowerLogger logger = new PrintStreamLogger(System.out);
logger.setSeverity(IFernflowerLogger.Severity.INFO);
```

---

## Padrões Comuns

### Pattern 1: Decompile Single File → String

```java
String decompile(File classFile) throws Exception {
    StringResultSaver saver = new StringResultSaver();
    BaseDecompiler d = new BaseDecompiler(
        (e,i) -> Files.readAllBytes(classFile.toPath()),
        saver,
        new HashMap<>(),
        new PrintStreamLogger(System.out)
    );
    d.addSource(classFile);
    d.decompileContext();
    return saver.getResult();
}
```

### Pattern 2: Decompile JAR → Filesystem

```java
void decompileJar(File jar, File outDir) throws Exception {
    BaseDecompiler d = new BaseDecompiler(
        new JarBytecodeProvider(jar),
        new FileSystemResultSaver(outDir.toPath()),
        defaultOptions(),
        new PrintStreamLogger(System.out)
    );
    d.addSource(jar);
    d.decompileContext();
}
```

### Pattern 3: Decompile with Dependencies

```java
void decompileWithDeps(File src, File rt, File outDir) throws Exception {
    BaseDecompiler d = new BaseDecompiler(
        new FileSystemBytecodeProvider("bin"),
        new FileSystemResultSaver(outDir.toPath()),
        defaultOptions(),
        new PrintStreamLogger(System.out)
    );
    d.addSource(src);
    d.addLibrary(rt);  // Add JDK as reference
    d.decompileContext();
}
```

### Pattern 4: Silent Processing

```java
String decompileSilent(File f) throws Exception {
    StringResultSaver saver = new StringResultSaver();
    BaseDecompiler d = new BaseDecompiler(
        (e,i) -> Files.readAllBytes(f.toPath()),
        saver,
        new HashMap<>(),
        new PrintStreamLogger(new java.io.ByteArrayOutputStream()) // Discard output
    );
    d.addSource(f);
    d.decompileContext();
    return saver.getResult();
}
```

---

## Common File Paths

```java
// Format: internalPath = "package/subpackage/ClassName.class"
"java/lang/String.class"           // java.lang.String
"com/example/MyClass.class"         // com.example.MyClass
"com/example/Outer$Inner.class"     // Inner class
```

---

## Error Handling

```java
try {
    decompiler.decompileContext();
} catch (Exception e) {
    logger.writeMessage(e.getMessage(), Severity.ERROR, e);
}
```

**Check if valid class**:
```java
byte[] data = Files.readAllBytes(classFile.toPath());
boolean isValid = data.length >= 4 && 
    data[0] == (byte)0xCA && data[1] == (byte)0xFE &&
    data[2] == (byte)0xBA && data[3] == (byte)0xBE;
```

---

## Default Options Template

```java
private Map<String, Object> getDefaultOptions() {
    Map<String, Object> map = new HashMap<>();
    map.put("rbr", "1");    // Hide bridge
    map.put("rsy", "1");    // Hide synthetic
    map.put("din", "1");    // Inner classes
    map.put("dc4", "1");    // 1.4 collapse
    map.put("das", "1");    // Assertions
    map.put("hes", "1");    // Hide empty super
    map.put("hdc", "1");    // Hide empty ctor
    map.put("dgs", "1");    // Generics
    map.put("rer", "1");    // Empty ranges
    map.put("den", "1");    // Enumerations
    map.put("udv", "1");    // Var names
    map.put("ump", "1");    // Param names
    map.put("ind", "    "); // 4 spaces
    map.put("nls", "1");    // Unix newlines
    map.put("log", "INFO"); // Log level
    return map;
}
```

---

## Maven Dependency

```xml
<dependency>
    <groupId>com.github.jetbrains</groupId>
    <artifactId>fernflower</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>

<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

---

## Command Line Usage

```bash
# Single class
java -jar fernflower.jar -rbr=1 -udv=1 MyClass.class output/

# JAR file
java -jar fernflower.jar -rbr=1 myapp.jar output/

# With JDK as reference
java -jar fernflower.jar -rbr=1 -e=rt.jar myapp.jar output/

# Options syntax
java -jar -<option>=<value> source destination
```

---

## Version Compatibility

| Java Target | Fernflower Version | Notes |
|-------------|-------------------|-------|
| Java 8 | 1.0.0+ | Supported |
| Java 11+ | 1.0.0+ | Recommended |
| Java 17+ | master-SNAPSHOT | Latest features |
| Java 21+ | master-SNAPSHOT | Full support |

---

## Debugging Checklist

```
[ ] IBytecodeProvider returns valid bytecode
    - First 4 bytes should be: CA FE BA BE
    - File size > 0
    
[ ] IResultSaver methods are called
    - Add logging to track method calls
    - Check if saveClassFile() is invoked
    
[ ] File paths are correct
    - External path format: "com/example/Class.class"
    - File on disk actually exists
    
[ ] Options are valid
    - All boolean values are "0" or "1"
    - No typos in option names
    
[ ] Logger shows meaningful messages
    - Set severity to TRACE for detailed output
    - Check for warnings/errors
```

---

## One-Liners

```java
// Decompile and print
System.out.println(decompileSilent(new File("MyClass.class")));

// Count methods
String code = decompileSilent(new File("MyClass.class"));
int methodCount = code.split("\\{").length - 1;

// Check if decompiled
String code = decompileSilent(new File("MyClass.class"));
boolean success = code.length() > 100 && code.contains("public");

// Extract class name
String className = code.split("class ")[1].split("[{\\s]")[0];

// Save to file
Files.write(Paths.get("MyClass.java"), 
    decompileSilent(new File("MyClass.class")).getBytes());
```

---

## See Also

- Full Reference: `FERNFLOWER_API_REFERENCE.md`
- Practical Examples: `FERNFLOWER_PRACTICAL_EXAMPLES.md`
- Troubleshooting: `FERNFLOWER_TROUBLESHOOTING.md`
