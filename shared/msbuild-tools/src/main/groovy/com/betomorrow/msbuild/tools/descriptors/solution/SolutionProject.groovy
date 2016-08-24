package com.betomorrow.msbuild.tools.descriptors.solution

class SolutionProject {

    def String name
    def String path

    SolutionProject(String name, String path) {
        this.name = name
        this.path = path
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        SolutionProject that = (SolutionProject) o

        if (name != that.name) return false
        if (path != that.path) return false

        return true
    }

    int hashCode() {
        int result
        result = (name != null ? name.hashCode() : 0)
        result = 31 * result + (path != null ? path.hashCode() : 0)
        return result
    }


    @Override
    public String toString() {
        return "SolutionProject{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}