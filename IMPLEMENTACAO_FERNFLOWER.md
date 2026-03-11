# Implementação do Decompilador Fernflower - Resumo

## 📋 O que foi criado

Este documento resume a implementação do suporte ao **Fernflower** no plugin nb-java-decompiler do Apache NetBeans.

### Arquivos Criados

#### 1. **DecompilerClassImpl.java** (Fernflower)
- **Caminho**: `src/main/java/io/github/moacirrf/javadecompiler/fernflower/DecompilerClassImpl.java`
- **Responsabilidade**: Implementa a interface `Decompiler<String, FileObject>` usando Fernflower
- **Funcionalidade**:
  - Decompila classes Java para código-fonte
  - Configura opções de decompilação (reconstruit nomes de variáveis, generics, etc.)
  - Retorna o código decompilado como String
  - Inclui cabeçalho de comentário indicando que foi decompilado

#### 2. **NetbeansBytecodeProviderImpl.java**
- **Caminho**: `src/main/java/io/github/moacirrf/javadecompiler/fernflower/NetbeansBytecodeProviderImpl.java`
- **Implementa**: `IBytecodeProvider` (interface Fernflower)
- **Responsabilidade**: Fornece o bytecode das classes para decompilação
- **Integração**: Usa o `FileSystemHelper` do NetBeans para acessar arquivos do filesystem

#### 3. **StringResultSaver.java**
- **Caminho**: `src/main/java/io/github/moacirrf/javadecompiler/fernflower/StringResultSaver.java`
- **Implementa**: `IResultSaver` (interface Fernflower)
- **Responsabilidade**: Captura o resultado da decompilação em um `StringBuilder`
- **Métodos obrigatórios implementados**:
  - `saveClassFile()` - Captura o código-fonte decompilado
  - `saveFolder()`, `saveDirEntry()`, `copyFile()`, etc. - Vazios (não usados para classe única)

### Atualização da Factory

#### **DecompilerFactory.java** (Atualizado)
- **Adição**: Enum `DecompilerType` com opcões `CFR` e `FERNFLOWER`
- **Novos métodos**:
  - `create()` - Usa o tipo padrão (CFR por compatibilidade)
  - `create(DecompilerType type)` - Cria decompilador do tipo especificado
  - `setDefaultType()` - Define o tipo padrão
  - `getDefaultType()` - Obtém o tipo padrão

### Dependência Maven

A dependência do Fernflower já estava no `pom.xml`:
```xml
<dependency>
    <groupId>com.github.jetbrains</groupId>
    <artifactId>fernflower</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```

---

## 🎯 Opcões de Decompilação Configuradas

As seguintes opções foram configuradas por padrão:

| Opção | Valor | Descrição |
|-------|-------|-----------|
| `rbr` | 1 | Hide bridge methods |
| `rsy` | 1 | Hide synthetic members |
| `din` | 1 | Decompile inner classes |
| `dc4` | 1 | Collapse 1.4 class references |
| `das` | 1 | Decompile assertions |
| `hes` | 1 | Hide empty super invocation |
| `hdc` | 1 | Hide empty default constructor |
| `dgs` | 1 | Decompile generic signatures |
| `rer` | 1 | Remove empty exception ranges |
| `den` | 1 | Decompile enumerations |
| `udv` | 1 | Reconstruct variable names |
| `ump` | 1 | Reconstruct parameter names |
| `ner` | 1 | Assume returns don't throw exceptions |
| `ind` | "    " | Indentation (4 spaces) |
| `nls` | 1 | Use \n for newlines |
| `log` | "WARN" | Log level |

---

## 🔧 Como Usar

### Usar Fernflower como decompilador primário:
```java
import io.github.moacirrf.javadecompiler.DecompilerFactory;
import io.github.moacirrf.javadecompiler.DecompilerFactory.DecompilerType;

// Usar Fernflower diretamente
Decompiler decompiler = DecompilerFactory.create(DecompilerType.FERNFLOWER);

// Ou definir como padrão
DecompilerFactory.setDefaultType(DecompilerType.FERNFLOWER);
Decompiler decompiler = DecompilerFactory.create();
```

### Utilizar CFR (padrão existente):
```java
// Continua funcionando como antes
Decompiler decompiler = DecompilerFactory.create();

// Ou explicitamente
Decompiler decompiler = DecompilerFactory.create(DecompilerType.CFR);
```

### Decompilação:
```java
FileObject classFile = ...;
Decompiler decompiler = DecompilerFactory.create(DecompilerType.FERNFLOWER);
String sourceCode = decompiler.decompile(classFile);
```

---

## 📚 Estrutura de Diretórios

```
src/main/java/io/github/moacirrf/javadecompiler/
├── cfr/                          (Decompilador CFR existente)
│   ├── DecompilerClassImpl.java
│   ├── NetbeansClassFileSourceImpl.java
│   └── PluginDumperFactory.java
│
├── fernflower/                   (Novo - Decompilador Fernflower)
│   ├── DecompilerClassImpl.java          ✨ NOVO
│   ├── NetbeansBytecodeProviderImpl.java ✨ NOVO
│   └── StringResultSaver.java           ✨ NOVO
│
├── files/
├── validator/
├── Decompiler.java              (Interface padrão)
├── DecompilerFactory.java       (Factory atualizada)
├── DecompileAction.java
└── ExceptionHandler.java
```

---

## ✅ Próximos Passos (Opcionais)

1. **Criar UI**: Adicionar menu/opção no NetBeans para escolher entre CFR e Fernflower
2. **Preferências**: Armazenar escolha do usuário em preferences
3. **Configuração**: Permitir customizar opções de decompilação por usuário
4. **Testes**: Adicionar testes unitários para validar ambos os decompiladores
5. **Documentação**: Adicionar documentação no README sobre as diferenças entre CFR e Fernflower

---

## ⚙️ Compatibilidade

- ✅ Java 11+ (projeto usa Java 11 como base)
- ✅ NetBeans 13.0+ (baseado em dependências no pom.xml)
- ✅ Compatível com estrutura de filesystem do NetBeans via `FileSystemHelper`
- ✅ Reutiliza mecanismos de tratamento de exceção existentes

---

## 📝 Notas Importantes

1. **Compatibilidade do Fernflower**: A versão usada é `master-SNAPSHOT` do repositório JetBrains Fernflower
2. **Integração Suave**: Ambos os decompiladores (CFR e Fernflower) seguem o mesmo padrão de implementação
3. **Sem Breaking Changes**: A mudança é totalmente compatível com o código existente
4. **CFR Permanece Padrão**: CFR continua sendo o decompilador padrão se não especificado

---

## 🧪 Validação Realizada

- ✅ Implementação segue o padrão de interfaces do Fernflower
- ✅ Todos os métodos obrigatórios de `IResultSaver` implementados
- ✅ `IBytecodeProvider` corretamente integrado com FileSystemHelper do NetBeans
- ✅ Factory pattern atualizado para suportar múltiplos decompiladores
- ✅ Compatibilidade com Java 11 (sem usar switch expressions)
- ✅ Sem parâmetros não utilizados ou imports desnecessários

---

Criado em: 11 de março de 2026
Versão: 1.0
Autor: GitHub Copilot
