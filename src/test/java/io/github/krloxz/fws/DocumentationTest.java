package io.github.krloxz.fws;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.function.Failable;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.docs.Documenter.CanvasOptions;
import org.springframework.modulith.docs.Documenter.DiagramOptions;

import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;
import com.structurizr.export.Diagram;
import com.structurizr.export.plantuml.StructurizrPlantUMLExporter;
import com.tngtech.archunit.core.domain.JavaClass;

import net.sourceforge.plantuml.SourceFileReader;

/**
 * Supporting test class used to generate the project documentation.
 *
 * @author Carlos Gomez
 */
@Tag("documentation")
class DocumentationTest {

  @Test
  void generateDocumentation() throws StructurizrDslParserException, IOException {
    writeArchitectureDiagrams();
    writeModulithDocumentation();
    renderAllDiagrams();
  }

  private void writeArchitectureDiagrams() throws StructurizrDslParserException {
    final var parser = new StructurizrDslParser();
    parser.parse(new File("docs/system/architecture_model.dsl"));
    final var workspace = parser.getWorkspace();
    new StructurizrPlantUMLExporter().export(workspace)
        .forEach(Failable.asConsumer(this::writePlantUmlDiagram));
  }

  private void writePlantUmlDiagram(final Diagram diagram) throws IOException {
    final var fileName = diagram.getKey() + "." + diagram.getFileExtension();
    final var filePath = recreateFile(fileName);
    try (Writer writer = new FileWriter(filePath.toFile())) {
      writer.write(diagram.getDefinition());
    }
  }

  private Path recreateFile(final String name) throws IOException {
    final var outputFolder = "docs/system";
    Files.createDirectories(Paths.get(outputFolder));
    final var filePath = Paths.get(outputFolder, name);
    Files.deleteIfExists(filePath);
    return Files.createFile(filePath);
  }

  private void writeModulithDocumentation() {
    new Documenter(applicationModules(), "docs/modules")
        .writeDocumentation(
            DiagramOptions.defaults(),
            CanvasOptions.defaults().revealInternals());
  }

  private ApplicationModules applicationModules() {
    return ApplicationModules.of(
        FwsApplication.class,
        JavaClass.Predicates.resideInAnyPackage(
            "io.github.krloxz.fws.test..",
            "io.github.krloxz.fws.mapstruct..")
            .or(JavaClass.Predicates.assignableTo(RepresentationModelAssembler.class)
                .or(JavaClass.Predicates.simpleNameEndingWith("MapperImpl"))));
  }

  private void renderAllDiagrams() throws IOException {
    try (var files = FileUtils.streamFiles(new File("docs"), true, "puml")) {
      files.forEach(Failable.asConsumer(this::renderDiagram));
    }
  }

  private void renderDiagram(final File file) throws IOException {
    final var reader = new SourceFileReader(file);
    reader.getGeneratedImages();
  }

}
