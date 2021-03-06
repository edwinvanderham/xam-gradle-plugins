package com.betomorrow.gradle.application.extensions

import com.betomorrow.xamarin.ios.plist.DefaultInfoPlistReader
import com.betomorrow.xamarin.ios.plist.InfoPlist
import com.betomorrow.xamarin.ios.plist.InfoPlistReader
import com.betomorrow.xamarin.files.FileUtils
import com.betomorrow.xamarin.descriptors.project.ProjectDescriptor
import com.betomorrow.xamarin.descriptors.solution.SolutionDescriptor
import com.betomorrow.xamarin.descriptors.solution.SolutionLoader
import org.gradle.api.Project

class XamarinIosApplicationExtension {

    protected SolutionLoader solutionLoader = new SolutionLoader()
    protected  InfoPlistReader infoPlistReader = new DefaultInfoPlistReader()

    private static String IOS_SUFFIX = ".IOS"
    private static String DEFAULT_PLATFORM = "iPhone"

    String appName
    String output
    String infoPlist
    String projectFile
    String platform

    private @Lazy ProjectDescriptor pd = sd.getProject(getAppName())
    private @Lazy SolutionDescriptor sd = solutionLoader.load(project.file(applicationExtension.solution))
    private @Lazy InfoPlist readInfoPlist = infoPlistReader.read(getInfoPlist())

    private XamarinApplicationExtension applicationExtension

    private Project project

    XamarinIosApplicationExtension(Project project) {
        this.project = project
        applicationExtension = project.extensions.getByType(XamarinApplicationExtension)
    }

    String getAppName() {
        // 1. returns configured appName
        if (appName != null) {
            return appName
        }

        // 2. try to find an appName.IOS in solution file
        String defaultAppName = applicationExtension.appName + IOS_SUFFIX
        if (sd.containsProject(defaultAppName)) {
            return  defaultAppName
        }

        // 3. try to find a single ios project
        if (sd.hasSingleIosProject()) {
            return sd.firstIosProject.name
        }

        throw new IllegalArgumentException("Can't resolve android project, please specify it with appName")

    }

    String getProjectFile() {
        // 1. returns configured projectFile
        if (projectFile != null) {
            return projectFile
        }

        // 2. returns projectFile of android appName
        return pd.path
    }

    String getOutput() {
        // 1. returns configured output
        if (output != null) {
            return output
        }

        // 2. returns default value
        return "dist/${getAppName()}-${getBundleVersion()}.ipa"
    }

    String getInfoPlist() {
        // 1. returns configured manifest
        if (infoPlist != null) {
            return infoPlist
        }

        // 2. return manifest of projectFile
        def infoPlistPath = FileUtils.toUnixPath(pd.infoPlist)
        return pd.path.parent.resolve(infoPlistPath).toString()
    }


    String getBundleVersion() {
        // 1. returns configured bundleVersion
        if (applicationExtension.appVersion != null) {
            return applicationExtension.appVersion
        }

        // 2. returns the one in manifest
        return readInfoPlist.bundleVersion
    }

    String getBundleShortVersion() {
        // 1. returns configured versionCode
        if (applicationExtension.storeVersion != null) {
            return applicationExtension.storeVersion
        }

        // 2. returns the one in manifest
        return readInfoPlist.bundleShortVersion
    }

    String getBundleIdentifier() {
        // 1. returns configured bundleIdentifier
        if (applicationExtension.packageName != null) {
            return applicationExtension.packageName
        }

        // 2. returns the one in manifest
        return readInfoPlist.bundleIdentifier
    }

    String getPlatform() {
        // 1. returns configured platform
        if (platform != null) {
            return platform
        }

        // 2. return default value
        return DEFAULT_PLATFORM
    }
}
