package com.betomorrow.msbuild.tools.nuget.assemblies

import com.betomorrow.msbuild.tools.nuget.dependencies.Dependency

/**
 * Created by olivier on 29/01/16.
 */
class AssemblySet implements Set<Assembly> {

    @Delegate Set<Assembly> dependencies = new HashSet<>();

}
