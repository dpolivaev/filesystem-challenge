package org.dpolivaev.katas.filesystem;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.ImportOptions;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.Test;

public class ArchitectureTest {
    @Test
    public void packagesAreFreeOfCycles() {
        final JavaClasses classes = new ClassFileImporter().importClasspath(//
                new ImportOptions().with(new ImportOption.DontIncludeTests()).with(new ImportOption.DontIncludeJars()));
        final ArchRule rule = SlicesRuleDefinition.slices().matching("..filesystem.(**)").should().beFreeOfCycles();
        rule.check(classes);

    }
}
