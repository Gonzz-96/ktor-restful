package com.example

import com.example.Routes.PARAM_PRIORITY
import com.example.Routes.PARAM_TASK_NAME
import com.example.Routes.ROUTE_TASK_BY_NAME
import com.example.Routes.ROUTE_TASK_BY_PRIORITY
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

            get(ROUTE_TASK_BY_NAME) {
                val name = call.parameters[PARAM_TASK_NAME]
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

            get(ROUTE_TASK_BY_PRIORITY) {
                val priorityString = call.parameters[PARAM_PRIORITY]
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

object Routes {
    const val TASKS = "/tasks"

    const val PARAM_PRIORITY = "priority"
    const val PARAM_TASK_NAME = "taskName"

    const val ROUTE_TASK_BY_PRIORITY = "/byPriority/{$PARAM_PRIORITY}"
    const val ROUTE_TASK_BY_NAME = "/byName/{$PARAM_TASK_NAME}"


}