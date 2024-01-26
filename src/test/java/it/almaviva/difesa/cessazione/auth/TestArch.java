package it.almaviva.difesa.cessazione.auth;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "it.almaviva.difesa.cessazione.auth",
        importOptions = { ImportOption.DoNotIncludeTests.class, ImportOption.DoNotIncludeJars.class })
class TestArch {

    /**
     * Check if controllers in packages ..controller.. aren't in services and Repositories in packages
     */
    @ArchTest
    final static ArchRule services_and_Repositories_should_not_depend_on_WebLayer =
        noClasses()
            .that().resideInAnyPackage("..service..")
                .or().resideInAnyPackage("..data..")
            .should()
                .dependOnClassesThat()
                .resideInAnyPackage("..controller..")
            .because("Services and repositories should not depend on web layer");
}
