package com.example

import com.example.com.example.model.TaskRepository
import com.example.model.Priority
import com.example.model.Task
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        staticResources("/", "static")

        route(Routes.TASKS) {
            get {
                call.respond(
                    listOf(
                        Task("cleaning", "Clean the house", Priority.Low),
                        Task("gardening", "Mow the lawn", Priority.Medium),
                        Task("shopping", "Buy the groceries", Priority.High),
                        Task("painting", "Paint the fence", Priority.Medium)
                    )
                )
            }

            get("/byName/{taskName}") {
                val name = call.parameters["taskName"]
                if (name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val task = TaskRepository.taskByName(name)
                if (task == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(task)
            }

            get("/byPriority/{priority}") {
                val priorityString = call.parameters["priority"]
                if (priorityString == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                try {
                    val priority = Priority.valueOf(priorityString)
                    val tasks = TaskRepository.tasksByPriority(priority)
                    if (tasks.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    call.respond(tasks)
                } catch (_: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}

private object Routes {
    const val TASKS = "/tasks"
}