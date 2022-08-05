package com.github.xianyuntang.prettier.services

import com.intellij.openapi.project.Project
import com.github.xianyuntang.prettier.Bundle

class ProjectService(project: Project) {

    init {
        println(Bundle.message("projectService", project.name))
    }
}
