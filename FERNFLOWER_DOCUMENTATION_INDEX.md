# 📚 Índice de Documentação Fernflower - Mapa Completo

## 🗺️ Navegação Rápida

```
┌─────────────────────────────────────────────────────────────────┐
│                  DOCUMENTAÇÃO FERNFLOWER                        │
│                  ══════════════════════════                      │
└─────────────────────────────────────────────────────────────────┘

START HERE (Comece aqui)
    ↓
    ├─ Preciso de um resumo rápido?
    │  → FERNFLOWER_RESEARCH_SUMMARY.md
    │    (5 min de leitura)
    │
    ├─ Preciso copiar código agora?
    │  → FERNFLOWER_QUICK_REFERENCE.md
    │    (snippets prontos)
    │
    ├─ Preciso entender a arquitetura?
    │  → FERNFLOWER_API_REFERENCE.md
    │    (referência completa)
    │
    ├─ Tenho um problema pra resolver?
    │  → FERNFLOWER_TROUBLESHOOTING.md
    │    (checklist de debug)
    │
    └─ Preciso de exemplos executáveis?
       → FERNFLOWER_PRACTICAL_EXAMPLES.md
         (código real, padrões)
```

---

## 📄 Descrição de Cada Arquivo

### 1. FERNFLOWER_RESEARCH_SUMMARY.md
**"Qual arquivo devo ler?"**

```
🎯 Propósito:     Sumário executivo de toda a pesquisa
⏱️  Tempo de leitura: 5-10 minutos
📊 Conteúdo:      Respostas diretas às 5 perguntas
🔍 Ideal para:    Entender o panorama geral
```

**Contém**:
- Resposta a cada uma de suas 5 perguntas específicas
- Checklist de implementação
- Problemas potenciais identificados
- Próximos passos recomendados

**Leia quando**: Começar do zero ou recapitular

---

### 2. FERNFLOWER_QUICK_REFERENCE.md
**"Preciso de código agora"**

```
🎯 Propósito:     Cheat sheet com snippets prontos
⏱️  Tempo de leitura: 2-3 minutos (consultivo)
📊 Conteúdo:      Padrões, opções, one-liners
🔍 Ideal para:    Copy-paste rápido
```

**Contém**:
- Fluxo mínimo (5 linhas = decompilação funcional)
- Tabelas de referência (opções, métodos, erros)
- Padrões comuns (4 padrões prontos)
- One-liners para tarefas específicas

**Use quando**: Codificando e apenas consultar valores

---

### 3. FERNFLOWER_API_REFERENCE.md
**"Preciso entender tudo"**

```
🎯 Propósito:     Referência completa da API
⏱️  Tempo de leitura: 30-45 minutos
📊 Conteúdo:      Documentação detalhada
🔍 Ideal para:    Aprender profundamente
```

**Contém**:
- Arquitetura geral (2 diagramas)
- IBytecodeProvider (explicação + 2 exemplos)
- IResultSaver (explicação + 2 exemplos)
- IFernflowerLogger (explicação + exemplo)
- BaseDecompiler (construtores + métodos)
- 50+ opções de decompilação documentadas
- Fluxo completo de 10 passos
- Tratamento de erros

**Leia quando**: Primeira vez compreendendo a API

---

### 4. FERNFLOWER_PRACTICAL_EXAMPLES.md
**"Quero ver código executável"**

```
🎯 Propósito:     Exemplos práticos e padrões
⏱️  Tempo de leitura: 20-30 minutos
📊 Conteúdo:      Código pronto pra usar
🔍 Ideal para:    Implementação real
```

**Contém**:
- Estrutura do seu projeto analisada
- Version melhorada de DecompilerClassImpl
- 5 implementações de IBytecodeProvider
- 3 implementações de IResultSaver
- Exemplo integrado: decompilação de JAR
- Tratamento robusto de erros
- Caching e otimizações
- Testes unitários

**Use quando**: Implementar integração real

---

### 5. FERNFLOWER_TROUBLESHOOTING.md
**"Algo não está funcionando"**

```
🎯 Propósito:     Resolução de problemas
⏱️  Tempo de leitura: 10-20 minutos
📊 Conteúdo:      11 problemas comuns + soluções
🔍 Ideal para:    Debugar erros
```

**Contém**:
- 11 problemas comuns com soluções
- Matriz de compatibilidade de versão
- Checklist de debug passo-a-passo
- Validação de bytecode
- Testes de debug específicos

**Use quando**: Erro de compilação ou execução

---

## 🔄 Fluxo de Leitura Recomendado

### Cenário 1: "Sou Novo em Fernflower"
```
1. FERNFLOWER_RESEARCH_SUMMARY.md    (entender conceitos)
   ↓
2. FERNFLOWER_API_REFERENCE.md       (aprender arqu arquitetura)
   ↓
3. FERNFLOWER_PRACTICAL_EXAMPLES.md  (implementar)
   ↓
4. FERNFLOWER_TROUBLESHOOTING.md     (quando errar)
```

### Cenário 2: "Estou Corrigindo um Erro"
```
1. FERNFLOWER_QUICK_REFERENCE.md     (refrescar memória)
   ↓
2. FERNFLOWER_TROUBLESHOOTING.md     (seu erro específico)
   ↓
3. FERNFLOWER_PRACTICAL_EXAMPLES.md  (implementação correta)
```

### Cenário 3: "Preciso Consultar Algo"
```
1. FERNFLOWER_QUICK_REFERENCE.md     (tabelas de valores)
   OU
2. FERNFLOWER_API_REFERENCE.md       (seções específicas)
```

### Cenário 4: "Copiando um Padrão"
```
1. FERNFLOWER_PRACTICAL_EXAMPLES.md  (padrões similares)
   ↓
2. FERNFLOWER_QUICK_REFERENCE.md     (valores exatos)
   ↓
3. FERNFLOWER_TROUBLESHOOTING.md     (validação)
```

---

## 📍 Mapa de Tópicos por Documento

### IBytecodeProvider
- **Explicação**: API_REFERENCE.md seção 2
- **Exemplos**: PRACTICAL_EXAMPLES.md seção 3
- **Problemas**: TROUBLESHOOTING.md #3, #6
- **Referência**: QUICK_REFERENCE.md tabela IBytecodeProvider

### IResultSaver
- **Explicação**: API_REFERENCE.md seção 3
- **Exemplos**: PRACTICAL_EXAMPLES.md seção 4
- **Problemas**: TROUBLESHOOTING.md #2
- **Referência**: QUICK_REFERENCE.md tabela IResultSaver

### BaseDecompiler
- **Explicação**: API_REFERENCE.md seção 5
- **Uso**: PRACTICAL_EXAMPLES.md seção 1-2
- **Problemas**: TROUBLESHOOTING.md #4, #7
- **Referência**: QUICK_REFERENCE.md pattern 1-4

### Opções de Decompilação
- **Lista completa**: API_REFERENCE.md seção 7
- **Recomendações**: RESEARCH_SUMMARY.md tabela
- **Tabela rápida**: QUICK_REFERENCE.md "Opções Mais Usadas"

### Tratamento de Erros
- **Conceituação**: API_REFERENCE.md seção 8
- **Exemplos**: PRACTICAL_EXAMPLES.md seção 8
- **Soluções**: TROUBLESHOOTING.md seções 1-11
- **Checklist**: TROUBLESHOOTING.md bottom "Checklist"

### Performance
- **Otimizações**: PRACTICAL_EXAMPLES.md seção 7
- **Problemas**: TROUBLESHOOTING.md #10
- **Configuração**: API_REFERENCE.md seção 7 (opção `mpm`)

---

## 🎯 Matriz de Ajuda (Tipo de Pergunta → Resposta)

| Pergunta | Resposta | Local |
|----------|----------|-------|
| O que é IBytecodeProvider? | API_REFERENCE.md:2 | Seção 2 |
| Como implementar IBytecodeProvider? | PRACTICAL_EXAMPLES.md:3 | Seção 3 |
| Meu IBytecodeProvider retorna null | TROUBLESHOOTING.md:6 | Problema #6 |
| Qual é a assinatura certa? | RESEARCH_SUMMARY.md | Problema Potencial #1 |
| Como usar BaseDecompiler? | QUICK_REFERENCE.md | Fluxo 5 passos |
| Qual opção ativa generics? | QUICK_REFERENCE.md | Tabela opções |
| Como decompiliar um JAR? | PRACTICAL_EXAMPLES.md:5 | Exemplo integrado |
| Teste não funciona | TROUBLESHOOTING.md | Seção debug |
| Preciso fazer cache | PRACTICAL_EXAMPLES.md:7 | Caching |
| OneLiners úteis? | QUICK_REFERENCE.md | Seção bottom |

---

## 🔑 Palavras-Chave por Arquivo

### API_REFERENCE.md
`arquitetura, interfaces, métodos, assinaturas, opções, fluxo, tratamento erros`

### PRACTICAL_EXAMPLES.md
`implementação, código, padrões, exemplos, casosuso, testes, otimizações`

### TROUBLESHOOTING.md
`erros, problemas, soluções, debug, validação, compatibilidade`

### QUICK_REFERENCE.md
`snippets, one-liners, tabelas, referência, padrões, templates`

### RESEARCH_SUMMARY.md
`resumo, respostas, checklist, recomendações, próximos passos`

---

## 🔍 Índice por Tópico

### Interfaces Principais
- IBytecodeProvider: API_REF.md:2, PRAC_EX.md:3
- IResultSaver: API_REF.md:3, PRAC_EX.md:4  
- IFernflowerLogger: API_REF.md:4

### Exemplos de Código
- Minimal: QUICK_REF.md (5 steps)
- Completo: PRAC_EX.md (seção 1-2)
- JAR: PRAC_EX.md (seção 5)
- Com deps: PRAC_EX.md (pattern 3)

### Opções Decompilação
- Mínimo: RESEARCH_SUMMARY.md (3 opções)
- Padrão: RESEARCH_SUMMARY.md (13 opções)
- Máximo: RESEARCH_SUMMARY.md (20+ opções)
- Referência: API_REF.md seção 7

### Problemas Conhecidos
- Override incorreto: TROUBLESHOOTING.md #1
- Resultado vazio: TROUBLESHOOTING.md #3, #6
- Arquivo inválido: TROUBLESHOOTING.md #7
- Timeout: TROUBLESHOOTING.md #10

### Verificações de Validação
- Magic number: TROUBLESHOOTING.md (debug section)
- Versão Java: TROUBLESHOOTING.md #7
- Compatibilidade: TROUBLESHOOTING.md (tabela)

---

## 🚀 Guia Rápido de Início (2 minutos)

```
1. Entender o fluxo imediato?
   → Leia: QUICK_REFERENCE.md "Fluxo Mínimo de 5 Passos"

2. Ver código pronto pra copiar?
   → Veja: PRACTICAL_EXAMPLES.md "Versão Melhorada" (seção 2)

3. Verificar opções corretas?
   → Consulte: QUICK_REFERENCE.md "Opções Mais Usadas"

4. Tem erro?
   → Procure em: TROUBLESHOOTING.md "Problemas Comuns"

PRONTO! 🎉
```

---

## 💾 Estrutura de Arquivos

```
nb-java-decompiler/
├── FERNFLOWER_RESEARCH_SUMMARY.md      ← COMECE AQUI (5 min)
├── FERNFLOWER_QUICK_REFERENCE.md       ← Consulta rápida (cheat sheet)
├── FERNFLOWER_API_REFERENCE.md         ← Completo & detalhado
├── FERNFLOWER_PRACTICAL_EXAMPLES.md    ← Código pronto
├── FERNFLOWER_TROUBLESHOOTING.md       ← Resolução de erros
└── FERNFLOWER_DOCUMENTATION_INDEX.md   ← ESTE ARQUIVO
    (você está aqui 👈)
```

---

## 📞 Suporte

Se ainda tiver dúvidas depois de ler:

1. **Verificar documentação oficial**: 
   https://github.com/JetBrains/intellij-community/tree/master/plugins/java-decompiler

2. **Ver issues relacionados**:
   https://youtrack.jetbrains.com/issues?q=project:IDEA%20subsystem:%22Java.%20Decompiler.%20Engine%22

3. **Fortges/alternativas**:
   https://github.com/MinecraftForge/ForgeFlower

4. **Stack Overflow**:
   Tag `fernflower` para perguntas comunitárias

---

## ✅ Última Verificação

- [ ] Li FERNFLOWER_RESEARCH_SUMMARY.md (respostas às 5 perguntas)
- [ ] Consultei QUICK_REFERENCE.md (valores e padrões)
- [ ] Copiei código de PRACTICAL_EXAMPLES.md (implementação)
- [ ] Validei em TROUBLESHOOTING.md (sem erros de compilação)
- [ ] Compilei e testei (funciona!)

**Se tudo OK**: Projeto pronto! ✨

---

**Documentação Completa da API Fernflower**  
Versão: 1.0  
Data: Março 2026  
Base: JetBrains intellij-community (master-SNAPSHOT)

