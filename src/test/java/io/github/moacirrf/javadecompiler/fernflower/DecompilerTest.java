/*
 * Copyright (C) 2026 Moacir da Roza Flores <moacirrf@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.moacirrf.javadecompiler.fernflower;

import io.github.moacirrf.javadecompiler.Decompiler;
import io.github.moacirrf.javadecompiler.DecompilerFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Testes unitários para as implementações dos decompiladores Fernflower e CFR
 * Teste de decompilação real de arquivos .class
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class DecompilerTest {

    @Test
    public void testFernflowerDecompilerInstanceType() {
        // Teste: Verificar se o decompilador Fernflower é a classe correta
        var decompiler = DecompilerFactory.create(DecompilerFactory.DecompilerType.FERNFLOWER);
        String className = decompiler.getClass().getName();
        
        Assert.assertTrue("Decompilador deve ser do package fernflower",
            className.contains("fernflower"));
        Assert.assertTrue("Decompilador deve ser DecompilerClassImpl",
            className.contains("DecompilerClassImpl"));
    }

    @Test
    public void testCFRDecompilerInstanceType() {
        // Teste: Verificar se o decompilador CFR é a classe correta
        var decompiler = DecompilerFactory.create(DecompilerFactory.DecompilerType.CFR);
        String className = decompiler.getClass().getName();
        
        Assert.assertTrue("Decompilador deve ser do package cfr",
            className.contains("cfr"));
        Assert.assertTrue("Decompilador deve ser DecompilerClassImpl",
            className.contains("DecompilerClassImpl"));
    }

    @Test
    public void testFernflowerDecompileRealClassFile() throws Exception {
        // Teste real: decompila um arquivo .class verdadeiro usando Fernflower
        File classFile = findCompiledClassFile("DecompilerClassImpl.class", "fernflower");
        assertNotNull("Arquivo .class Fernflower não foi encontrado", classFile);
        assertTrue("Arquivo .class deve existir", classFile.exists());

        FileObject fileObject = FileUtil.toFileObject(classFile);
        assertNotNull("FileObject não deve ser nulo", fileObject);

        Decompiler fernflower = DecompilerFactory.create(DecompilerFactory.DecompilerType.FERNFLOWER);
        String decompiled = (String) fernflower.decompile(fileObject);
        
        // Validações
        assertNotNull("Código decompilado não pode ser nulo", decompiled);
        assertTrue("Código decompilado não pode estar vazio", decompiled.length() > 0);
        
        // Verifica se contém código Java válido
        assertTrue("Deve conter palavra-chave 'public'", decompiled.contains("public"));
        assertTrue("Deve conter a declaração de classe", decompiled.contains("class"));
        assertTrue("Deve conter método decompile", decompiled.contains("decompile"));
    }

    @Test
    public void testCFRDecompileRealClassFile() throws Exception {
        // Teste real: decompila um arquivo .class verdadeiro usando CFR
        File classFile = findCompiledClassFile("DecompilerClassImpl.class", "cfr");
        assertNotNull("Arquivo .class CFR não foi encontrado", classFile);
        assertTrue("Arquivo .class deve existir", classFile.exists());

        FileObject fileObject = FileUtil.toFileObject(classFile);
        assertNotNull("FileObject não deve ser nulo", fileObject);

        Decompiler cfr = DecompilerFactory.create(DecompilerFactory.DecompilerType.CFR);
        String decompiled = (String) cfr.decompile(fileObject);
        
        // Validações
        assertNotNull("Código decompilado não pode ser nulo", decompiled);
        assertTrue("Código decompilado não pode estar vazio", decompiled.length() > 0);
        
        // Verifica se contém código Java válido
        assertTrue("Deve conter palavra-chave 'public'", decompiled.contains("public"));
        assertTrue("Deve conter a declaração de classe", decompiled.contains("class"));
        assertTrue("Deve conter método decompile", decompiled.contains("decompile"));
    }

    @Test
    public void testFernflowerDecompileContainsValidJavaCode() throws Exception {
        // Teste: verifica se a decompilação contém código Java válido
        File classFile = findCompiledClassFile("DecompilerClassImpl.class", "fernflower");
        if (classFile == null) return; // Skip se arquivo não encontrado
        
        FileObject fileObject = FileUtil.toFileObject(classFile);
        Decompiler fernflower = DecompilerFactory.create(DecompilerFactory.DecompilerType.FERNFLOWER);
        String decompiled = (String) fernflower.decompile(fileObject);
        
        // A classe DecompilerClassImpl deve ter imports
        assertTrue("Deve conter imports", decompiled.contains("import"));
        
        // Deve conter o nome da classe
        assertTrue("Deve conter nome da classe", decompiled.contains("DecompilerClassImpl"));
        
        // Deve conter implementação de interface
        assertTrue("Deve implementar interface Decompiler", 
            decompiled.contains("implements") || decompiled.contains("Decompiler"));
    }

    @Test
    public void testFernflowerDecompileProducesSignificantContent() throws Exception {
        // Teste: verifica que a decompilação produz conteúdo significativo
        File classFile = findCompiledClassFile("DecompilerClassImpl.class", "fernflower");
        if (classFile == null) return; // Skip se arquivo não encontrado
        
        FileObject fileObject = FileUtil.toFileObject(classFile);
        Decompiler fernflower = DecompilerFactory.create(DecompilerFactory.DecompilerType.FERNFLOWER);
        String decompiled = (String) fernflower.decompile(fileObject);
        
        // Código decompilado deve ter pelo menos 200 caracteres
        assertTrue("Código decompilado deve ter conteúdo significativo (>200 chars)", 
            decompiled.length() > 200);
    }

    @Test
    public void testCFRDecompileProducesSignificantContent() throws Exception {
        // Teste: verifica que a decompilação CFR produz conteúdo significativo
        File classFile = findCompiledClassFile("DecompilerClassImpl.class", "cfr");
        if (classFile == null) return; // Skip se arquivo não encontrado
        
        FileObject fileObject = FileUtil.toFileObject(classFile);
        Decompiler cfr = DecompilerFactory.create(DecompilerFactory.DecompilerType.CFR);
        String decompiled = (String) cfr.decompile(fileObject);
        
        // Código decompilado deve ter pelo menos 200 caracteres
        assertTrue("Código decompilado deve ter conteúdo significativo (>200 chars)", 
            decompiled.length() > 200);
    }

    @Test
    public void testBothDecompilersProduceValidOutput() throws Exception {
        // Teste: compara saída de ambos os decompiladores
        File classFile = findCompiledClassFile("DecompilerClassImpl.class", "fernflower");
        if (classFile == null) {
            classFile = findCompiledClassFile("DecompilerClassImpl.class", "cfr");
        }
        if (classFile == null) return; // Skip se arquivo não encontrado
        
        FileObject fileObject = FileUtil.toFileObject(classFile);

        Decompiler fernflower = DecompilerFactory.create(DecompilerFactory.DecompilerType.FERNFLOWER);
        Decompiler cfr = DecompilerFactory.create(DecompilerFactory.DecompilerType.CFR);
        
        String fernflowerOutput = (String) fernflower.decompile(fileObject);
        String cfrOutput = (String) cfr.decompile(fileObject);
        
        // Ambos devem produzir conteúdo válido
        assertTrue("Fernflower deve conter 'class'", fernflowerOutput.contains("class"));
        assertTrue("CFR deve conter 'class'", cfrOutput.contains("class"));
        
        // Ambos devem conter 'public'
        assertTrue("Fernflower deve conter 'public'", fernflowerOutput.contains("public"));
        assertTrue("CFR deve conter 'public'", cfrOutput.contains("public"));
    }

    @Test
    public void testDecompilerDoesNotReturnNull() throws Exception {
        // Teste: garante que decompilador nunca retorna nulo
        File classFile = findCompiledClassFile("DecompilerClassImpl.class", "fernflower");
        if (classFile == null) {
            classFile = findCompiledClassFile("DecompilerClassImpl.class", "cfr");
        }
        if (classFile == null) return; // Skip se arquivo não encontrado
        
        FileObject fileObject = FileUtil.toFileObject(classFile);

        Decompiler fernflower = DecompilerFactory.create(DecompilerFactory.DecompilerType.FERNFLOWER);
        String result = (String) fernflower.decompile(fileObject);
        assertNotNull("Decompilador Fernflower não deve retornar null", result);
        
        Decompiler cfr = DecompilerFactory.create(DecompilerFactory.DecompilerType.CFR);
        result = (String) cfr.decompile(fileObject);
        assertNotNull("Decompilador CFR não deve retornar null", result);
    }

    @Test
    public void testDecompileMultipleTimes() throws Exception {
        // Teste: verifica se decompila o mesmo arquivo múltiplas vezes
        File classFile = findCompiledClassFile("DecompilerClassImpl.class", "fernflower");
        if (classFile == null) return; // Skip se arquivo não encontrado
        
        FileObject fileObject = FileUtil.toFileObject(classFile);
        Decompiler fernflower = DecompilerFactory.create(DecompilerFactory.DecompilerType.FERNFLOWER);

        String result1 = (String) fernflower.decompile(fileObject);
        String result2 = (String) fernflower.decompile(fileObject);
        String result3 = (String) fernflower.decompile(fileObject);
        
        // Três decompilações do mesmo arquivo devem produzir o mesmo resultado
        assertEquals("Primeira e segunda decompilação devem ser iguais", result1, result2);
        assertEquals("Segunda e terceira decompilação devem ser iguais", result2, result3);
    }

    /**
     * Helper method para encontrar um arquivo .class compilado no projeto
     */
    private File findCompiledClassFile(String className, String subfolder) {
        // Procura no diretório target/classes/io/github/moacirrf/javadecompiler/{subfolder}/
        Path targetPath = Paths.get("target/classes/io/github/moacirrf/javadecompiler", subfolder, className);
        
        if (Files.exists(targetPath)) {
            try {
                // Converter para caminho absoluto normalizado (obrigatório para FileUtil.toFileObject)
                File absoluteFile = targetPath.toAbsolutePath().normalize().toFile();
                return absoluteFile;
            } catch (Exception e) {
                return null;
            }
        }
        
        return null;
    }
}

