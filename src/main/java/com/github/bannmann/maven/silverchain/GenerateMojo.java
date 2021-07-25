package com.github.bannmann.maven.silverchain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import silverchain.Silverchain;
import silverchain.SilverchainException;
import silverchain.generator.JavaGenerator;
import silverchain.parser.ParseException;
import silverchain.validator.JavaValidator;

/**
 * Invokes Silverchain for an {@code .ag} file.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateMojo extends AbstractMojo
{
    /**
     * Input grammar file.
     */
    @Parameter(property = "silverchain.inputFile", required = true)
    private File inputFile;

    /**
     * Output directory.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/silverchain",
        property = "silverchain.outputDirectory",
        required = true)
    private File outputDirectory;

    /**
     * Javadoc source directory.
     */
    @Parameter(defaultValue = "${project.build.sourceDirectory}",
        property = "silverchain.javadocSourceDirectory",
        required = true)
    private File javadocSourceDirectory;

    /**
     * Max number of generated files.
     */
    @Parameter(defaultValue = "500", property = "maxFileCount", required = true)
    private int maxFileCount;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    public void execute() throws MojoExecutionException
    {
        Silverchain silverchain = new Silverchain();
        silverchain.generatorProvider(JavaGenerator::new);
        silverchain.validatorProvider(JavaValidator::new);
        silverchain.warningHandler(warning -> getLog().warn(warning.toString()));

        Path outputDirectoryPath = outputDirectory.toPath();
        silverchain.outputDirectory(outputDirectoryPath);

        silverchain.maxFileCount(maxFileCount);

        try (InputStream inputStream = Files.newInputStream(inputFile.toPath()))
        {
            silverchain.run(inputStream,
                javadocSourceDirectory.toPath()
                    .toAbsolutePath()
                    .toString());
        }
        catch (IOException | ParseException | SilverchainException e)
        {
            throw new MojoExecutionException("Could not generate API", e);
        }

        project.addCompileSourceRoot(outputDirectoryPath.toString());
    }
}
