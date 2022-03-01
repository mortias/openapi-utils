package com.github.mortias.apimerge.services

import com.intellij.openapi.project.Project
import com.github.mortias.apimerge.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
