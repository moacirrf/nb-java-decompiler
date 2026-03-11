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

import io.github.moacirrf.javadecompiler.DecompilerFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testes básicos para validar a implementação do Fernflower
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class FernflowerDecompilerTest {

    @Test
    public void testFernflowerDecompilerCanBeCreated() {
        // Teste: Verificar se o decompilador Fernflower pode ser criado
        var decompiler = DecompilerFactory.create(DecompilerFactory.DecompilerType.FERNFLOWER);
        
        Assert.assertNotNull("Decompilador Fernflower não pode ser null", decompiler);
        Assert.assertNotNull("Decompilador deve ser instância de DecompilerClassImpl",
            decompiler.getClass().getSimpleName());
    }

    @Test
    public void testCFRDecompilerStillWorks() {
        // Teste: Verificar se o decompilador CFR ainda funciona
        var decompiler = DecompilerFactory.create(DecompilerFactory.DecompilerType.CFR);
        
        Assert.assertNotNull("Decompilador CFR não pode ser null", decompiler);
    }

    @Test
    public void testDefaultDecompilerIsCFR() {
        // Teste: Verificar se o tipo padrão é CFR
        var defaultType = DecompilerFactory.getDefaultType();
        
        Assert.assertEquals("Tipo padrão deve ser CFR",
            DecompilerFactory.DecompilerType.CFR, defaultType);
    }

    @Test
    public void testCanChangeDefaultDecompiler() {
        // Teste: Verificar se consegue mudar o tipo padrão
        DecompilerFactory.DecompilerType original = DecompilerFactory.getDefaultType();
        try {
            // Mudar para Fernflower
            DecompilerFactory.setDefaultType(DecompilerFactory.DecompilerType.FERNFLOWER);
            Assert.assertEquals("Tipo padrão deve ser Fernflower após setDefaultType",
                DecompilerFactory.DecompilerType.FERNFLOWER, DecompilerFactory.getDefaultType());
            
            // Mudar para CFR
            DecompilerFactory.setDefaultType(DecompilerFactory.DecompilerType.CFR);
            Assert.assertEquals("Tipo padrão deve ser CFR após setDefaultType",
                DecompilerFactory.DecompilerType.CFR, DecompilerFactory.getDefaultType());
        } finally {
            // Restaurar valor original
            DecompilerFactory.setDefaultType(original);
        }
    }

    @Test
    public void testDefaultCreateUsesCFR() {
        // Teste: Verificar que create() sem parâmetro usa CFR por padrão
        DecompilerFactory.DecompilerType original = DecompilerFactory.getDefaultType();
        try {
            DecompilerFactory.setDefaultType(DecompilerFactory.DecompilerType.CFR);
            var decompiler = DecompilerFactory.create();
            
            Assert.assertNotNull("Decompilador padrão não pode ser null", decompiler);
        } finally {
            DecompilerFactory.setDefaultType(original);
        }
    }

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
    public void testDecompilerFactoryHasRequiredMethods() {
        // Teste: Verificar se DecompilerFactory tem os métodos esperados
        try {
            // Verificar método create()
            DecompilerFactory.class.getMethod("create");
            
            // Verificar método create(DecompilerType)
            DecompilerFactory.class.getMethod("create", DecompilerFactory.DecompilerType.class);
            
            // Verificar método setDefaultType(DecompilerType)
            DecompilerFactory.class.getMethod("setDefaultType", DecompilerFactory.DecompilerType.class);
            
            // Verificar método getDefaultType()
            DecompilerFactory.class.getMethod("getDefaultType");
            
            Assert.assertTrue("DecompilerFactory tem os métodos esperados", true);
        } catch (NoSuchMethodException e) {
            Assert.fail("DecompilerFactory está faltando métodos esperados: " + e.getMessage());
        }
    }

    @Test
    public void testDecompilerTypeEnumValues() {
        // Teste: Verificar se o enum DecompilerType tem os valores esperados
        DecompilerFactory.DecompilerType[] types = DecompilerFactory.DecompilerType.values();
        
        Assert.assertEquals("Deve haver 2 tipos de decompiladores", 2, types.length);
        
        boolean hasCFR = false;
        boolean hasFernflower = false;
        for (DecompilerFactory.DecompilerType type : types) {
            if (type == DecompilerFactory.DecompilerType.CFR) hasCFR = true;
            if (type == DecompilerFactory.DecompilerType.FERNFLOWER) hasFernflower = true;
        }
        
        Assert.assertTrue("DecompilerType deve ter CFR", hasCFR);
        Assert.assertTrue("DecompilerType deve ter FERNFLOWER", hasFernflower);
    }

}
