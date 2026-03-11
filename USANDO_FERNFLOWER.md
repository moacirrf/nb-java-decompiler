# Usando Fernflower no NB Java Decompiler

## 📖 Visãoo Geral

O plugin NB Java Decompiler agora suporta **dois decompiladores Java**:
- **CFR** (padrão) - Segue a implementação original
- **Fernflower** (novo) - Decompilador oficial do JetBrains

## 🚀 Quickstart

### Em Código

```java
import io.github.moacirrf.javadecompiler.DecompilerFactory;
import io.github.moacirrf.javadecompiler.DecompilerFactory.DecompilerType;
import io.github.moacirrf.javadecompiler.Decompiler;
import org.openide.filesystems.FileObject;

// Exemplo 1: Usar Fernflower explicitamente
FileObject classFile = ...;
Decompiler decompiler = DecompilerFactory.create(DecompilerType.FERNFLOWER);
String sourceCode = decompiler.decompile(classFile);
System.out.println(sourceCode);

// Exemplo 2: Mudar o decompilador padrão para Fernflower
DecompilerFactory.setDefaultType(DecompilerType.FERNFLOWER);
Decompiler decompiler = DecompilerFactory.create();  // Usa Fernflower agora

// Exemplo 3: Voltar ao CFR (padrão original)
DecompilerFactory.setDefaultType(DecompilerType.CFR);
Decompiler decompiler = DecompilerFactory.create();  // Usa CFR
```

---

## 📚 Estrutura Criada

### Novos Arquivos

1. **DecompilerClassImpl.java** (Fernflower)
   - Implementa `Decompiler<String, FileObject>`
   - Orquestra a decompilação usando Fernflower
   - Configura opções otimizadas

2. **NetbeansBytecodeProviderImpl.java**
   - Implementa `IBytecodeProvider` (interface Fernflower)
   - Carrega bytecode via FileSystem do NetBeans

3. **StringResultSaver.java**
   - Implementa `IResultSaver` (interface Fernflower)
   - Captura resultado em `StringBuilder`

### Arquivo Modificado

- **DecompilerFactory.java**
  - Novo enum: `DecompilerType { CFR, FERNFLOWER }`
  - Novos métodos: `create(DecompilerType)`, `setDefaultType()`, `getDefaultType()`

---

## ⚙️ API Reference

### DecompilerFactory

```java
// Criar decompilador CFR
Decompiler cfr = DecompilerFactory.create();
// Equivalente a:
Decompiler cfr = DecompilerFactory.create(DecompilerType.CFR);

// Criar decompilador Fernflower
Decompiler fernflower = DecompilerFactory.create(DecompilerType.FERNFLOWER);

// Definir tipo padrão
DecompilerFactory.setDefaultType(DecompilerType.FERNFLOWER);

// Obter tipo padrão atual
DecompilerType current = DecompilerFactory.getDefaultType();
```

### Decompiler Interface

```java
interface Decompiler<T, P> {
    T decompile(P input);
}

// Para ambos os decompiladores:
// T = String (código-fonte decompilado)
// P = FileObject (arquivo .class do NetBeans)

String sourceCode = decompiler.decompile(fileObject);
```

---

## 🎯 Diferenças: CFR vs Fernflower

| Aspecto | CFR | Fernflower |
|---------|-----|-----------|
| **Mantido por** | Unbekn | JetBrains |
| **Foco** | Java moderno | Compatibilidade geral |
| **Variáveis** | Nomes melhorados | Nomes básicos |
| **Generics** | Suporte avançado | Suporte básico |
| **Performance** | Rápido | Estável |

---

## 🔧 Configuração

### Opções Padrão do Fernflower

```
rbr=1        (Hide bridge methods)
rsy=1        (Hide synthetic members)
din=1        (Decompile inner classes)
dc4=1        (Collapse 1.4 references)
das=1        (Decompile assertions)
dgs=1        (Decompile generics)
udv=1        (Reconstruct variable names)
ump=1        (Reconstruct parameter names)
ind="    "   (Indentation)
nls=1        (Unix line endings)
log=WARN     (Log level)
```

---

## ❓ FAQ

### P: Qual é o decompilador padrão?
R: **CFR**. Para preservar compatibilidade com versões anteriores.

### P: Como troco para Fernflower?
R: `DecompilerFactory.setDefaultType(DecompilerType.FERNFLOWER);`

### P: Posso usar Both em paralelo?
R: Sim, crie instâncias separadas de cada um.

### P: Como implemento um novo decompilador?
R: 
1. Implemente `Decompiler<String, FileObject>`
2. Crie suportes para `IBytecodeProvider` e `IResultSaver`
3. Registre na `DecompilerFactory`

---

## 📋 Exemplo Completo

```java
package io.github.moacirrf.javadecompiler.example;

import io.github.moacirrf.javadecompiler.DecompilerFactory;
import io.github.moacirrf.javadecompiler.DecompilerFactory.DecompilerType;
import io.github.moacirrf.javadecompiler.Decompiler;
import org.openide.filesystems.FileObject;

public class DecompilationExample {

    public static void main(String[] args) {
        // Carregar arquivo .class
        FileObject classFile = loadClassFile("com/example/MyClass.class");

        // ===== Usar CFR =====
        System.out.println("=== Decompilação com CFR ===");
        Decompiler cfrDecompiler = DecompilerFactory.create(DecompilerType.CFR);
        String cfrResult = cfrDecompiler.decompile(classFile);
        System.out.println(cfrResult);

        // ===== Usar Fernflower =====
        System.out.println("\n=== Decompilação com Fernflower ===");
        Decompiler ffDecompiler = DecompilerFactory.create(DecompilerType.FERNFLOWER);
        String ffResult = ffDecompiler.decompile(classFile);
        System.out.println(ffResult);

        // ===== Comparar =====
        System.out.println("\n=== Comparação ===");
        System.out.println("CFR size: " + cfrResult.length() + " chars");
        System.out.println("Fernflower size: " + ffResult.length() + " chars");
    }

    private static FileObject loadClassFile(String path) {
        // Implementar carregamento do arquivo
        return null;
    }
}
```

---

## 🧪 Testes

Para testar a implementação:

1. Compilar o projeto:
   ```bash
   mvn clean compile
   ```

2. Empacotar como NBM:
   ```bash
   mvn clean package
   ```

3. Instalar no NetBeans:
   - Abrir NetBeans
   - Tools → Plugins → Install NBM
   - Selecionar `target/nb-java-decompiler-2.0-SNAPSHOT.nbm`

---

## 📞 Suporte

Para problemas ou sugestões, consulte:
- Documentação: [IMPLEMENTACAO_FERNFLOWER.md](IMPLEMENTACAO_FERNFLOWER.md)
- Exemplos: [FERNFLOWER_PRACTICAL_EXAMPLES.md](FERNFLOWER_PRACTICAL_EXAMPLES.md)
- Troubleshooting: [FERNFLOWER_TROUBLESHOOTING.md](FERNFLOWER_TROUBLESHOOTING.md)

---

Última atualização: 11 de março de 2026
