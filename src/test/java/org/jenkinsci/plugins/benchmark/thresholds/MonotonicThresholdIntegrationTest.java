/**
 * MIT license
 * Copyright 2017 Autodesk, Inc.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package org.jenkinsci.plugins.benchmark.thresholds;

import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import org.jenkinsci.plugins.benchmark.core.BenchmarkPublisher;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Plugin test including the full workflow.
 *
 * @author Daniel Mercier
 * @ref https://wiki.jenkins.io/display/JENKINS/Unit+Test
 * @since 6/20/2017
 */
public class MonotonicThresholdIntegrationTest {

    private static String OS = System.getProperty("os.name").toLowerCase();

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void first() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();


        // Add fake result file to workspace
        String text = "{"
                + "     \"name\": \"result_1\","
                + "     \"description\": \"description_res_1\","
                + "     \"value\": false"
                + "}";
        if (isUnix()) {
            project.getBuildersList().add(new Shell("echo " + text + " > result.json"));
        } else {
            project.getBuildersList().add(new BatchFile("echo " + text + " > result.json"));
        }

        // Activate the plugin
        project.getPublishersList().add(new BenchmarkPublisher("result.json", "simplestSchema", true, "", ""));

        project.save();

        FreeStyleBuild build = project.scheduleBuild2(0).get();
        System.out.println(build.getDisplayName() + " completed");

        String outputfilename = build.getRootDir().getAbsolutePath() + File.separator + "BenchmarkResult.json";
        File file = new File(outputfilename);
        assert (file.exists());
    }

    private class ResultCreator extends TestBuilder {

        private String path, content;

        @Override
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener buildListener) throws InterruptedException, IOException {
            build.getWorkspace().child(this.path).write(this.content, "UTF-8");
            return true;
        }
    }

    @Test
    public void testIncreasingMonotonic() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        //project.setCustomWorkspace("workspace");

        final int numberOfMockedBuilds = 10;

        // Add fake result file to workspace
        double mockValue = 0.127511367281024;

        final File customSchemaFile = new File("custom_schemas/jenkins_benchmark_plugin_schema.json");

        ResultCreator creator = new ResultCreator();
        creator.path = "result.json";
        project.getBuildersList().add(creator);

        BenchmarkPublisher publisher = new BenchmarkPublisher(
                "result.json",
                "customSchema",
                true,
                "",
                customSchemaFile.getAbsolutePath()
        );

        // Activate the plugin
        project.getPublishersList().add(
                publisher);

        project.save();

        List<Result> results = new ArrayList<Result>();

        for (int buildIndex = 0; buildIndex < numberOfMockedBuilds; buildIndex++) {

            // Make mock value gradually larger
            mockValue = mockValue * 1.05;

            creator.content = "{\"test\": {\"Average recall\": " + mockValue + ", \"Average precision\": " + mockValue + ", \"Average F1\": " + mockValue + ",  \"thresholds\":[ {\"method\": \"deltamonotonic\", \"delta\": 0.1 }]}}";

            FreeStyleBuild build = project.scheduleBuild2(0).get();

            //assertEquals(Result.SUCCESS, build.getResult());

            System.out.println(build.getDisplayName() + " completed");

            results.add(build.getResult());
        }

        return;
    }


    public static boolean isMac() {
        return (OS.contains("mac") || OS.contains("darwin"));
    }

    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
    }

    public static boolean isSolaris() {
        return (OS.contains("sunos"));
    }

}