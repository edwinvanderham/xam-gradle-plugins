package com.betomorrow.gradle.application.tasks

import com.betomorrow.android.tools.manifest.AndroidManifest
import com.betomorrow.ios.tools.plist.InfoPlist
import com.betomorrow.ios.tools.plist.InfoPlistReader
import com.betomorrow.ios.tools.plist.InfoPlistWriter
import com.betomorrow.msbuild.tools.Files.FileCopier
import com.betomorrow.msbuild.tools.commands.CommandRunner
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import java.nio.file.Paths

class BuildIOSAppTaskTest extends Specification {

    FileCopier fileCopier = Mock();
    CommandRunner runner = Mock();
    InfoPlistWriter infoPlistWriter = Mock();

    BuildIOSAppTask task;


    def setup() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'xamarin-application-plugin'

        project.application {
            solution 'src/test/resources/CrossApp/CrossApp.sln'
        }

        project.evaluate();

        task = project.tasks.buildIOS;

        task.infoPlistWriter = infoPlistWriter;
        task.fileCopier = fileCopier;
        task.commandRunner = runner;
    }


    def "should update info.plist"() {
        given:
        InfoPlist capturedInfoPlist;
        String capturedInfoPlistOutput;

        when:
        task.build()

        then:
        1 * infoPlistWriter.write(_, _) >> { m, out ->
            capturedInfoPlist = m;
            capturedInfoPlistOutput = out;
        }

        assert capturedInfoPlist.bundleIdentifier == "com.sample.crossapp"
        assert capturedInfoPlist.bundleShortVersion == "2.6"
        assert capturedInfoPlist.bundleVersion == "1.0"
    }

    def "should run mdtool"() {
        given:
        CommandRunner.Cmd capturedCmd;

        when:
        task.build()

        then:
        1 * runner.run(_) >> { cmd ->
            capturedCmd = cmd[0];
            return 1;
        }

        assert capturedCmd.build() == ["mdtool", "build", "--configuration:Release|iPhone", "src/test/resources/CrossApp/CrossApp.sln"]
    }

    def "should copy to output"() {
        given:
        String capturedSrc;
        String capturedDst;

        when:
        task.build();

        then:
        1 * fileCopier.replace(_,_) >> { src, dst ->
            capturedSrc = src;
            capturedDst = dst;
        }

        assert Paths.get(capturedSrc) == Paths.get("src/test/resources/CrossApp/iOS/bin/iPhone/Release/CrossApp.iOS.ipa");
        assert Paths.get(capturedDst) == Paths.get("dist/CrossApp.iOS-1.0.ipa");
    }

    def "should do operations in the right order"() {
        when:
        task.build();

        then:
        1 * infoPlistWriter.write(_, _);

        then:
        1 * runner.run(_) >> 1;

        then:
        1 * fileCopier.replace(_,_);
    }

}