package com.github.xianyuntang.prettier.services

import com.intellij.openapi.project.Project
import com.github.xianyuntang.prettier.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
