package com.nitroxina.kanb.kanboardApi

// QUERYS
const val GET_ME =  "getMe"
const val GET_MY_DASHBOARD = "getMyDashboard"

//PROJECT
const val GET_MY_PROJECTS = "getMyProjects" // projects with details
const val GET_MY_PROJECTS_LIST = "getMyProjectsList" //simplified projects, only id => nome
const val GET_PROJECT_USER_ROLE = "getProjectUserRole"  // the projectRole of a user for a given project
const val GET_ASSIGNABLE_USERS = "getAssignableUsers"  // Users that can be assigned to a task for a project
const val GET_PROJECT_BY_ID = "getProjectById"
const val CREATE_PROJECT = "createProject"
const val UPDATE_PROJECT = "updateProject"
const val REMOVE_PROJECT = "removeProject"
const val ENABLE_PROJECT = "enableProject"
const val DISABLE_PROJECT = "disableProject"

//BOARD
const val GET_BOARD = "getBoard"

//COLUMN
const val GET_COLUMNS = "getColumns"
const val GET_COLUMN = "getColumn"
const val CHANGE_COLUMN_POSITION = "changeColumnPosition"
const val UPDATE_COLUMN = "updateColumn"
const val ADD_COLUMN = "addColumn"
const val REMOVE_COLUMN = "removeColumn"


//TASKS
const val GET_MY_OVERDUE_TASKS = "getMyOverdueTasks"
const val GET_OVERDUE_TASKS = "getOverdueTasks"
const val GET_OVERDUE_TASKS_BY_PROJECT = "getOverdueTasksByProject"

const val CREATE_TASK = "createTask"
const val GET_TASK = "getTask"
const val GET_TASK_BY_REFERENCE = "getTaskByReference"
const val GET_ALL_TASK = "getAllTasks"
const val UPDATE_TASK = "updateTask"
const val OPEN_TASK = "openTask"
const val CLOSE_TASK = "closeTask"
const val REMOVE_TASK = "removeTask"
const val MOVE_TASK_POSITION = "moveTaskPosition"
const val SEARCH_TASK = "searchTasks"

//CATEGORY

const val GET_ALL_CATEGORIES = "getAllCategories" //for project
const val GET_CATEGORY = "getCategory"
const val CREATE_CATEGORY = "createCategory"
const val UPDATE_CATEGORY = "updateCategory"
const val REMOVE_CATEGORY = "removeCategory"

//TAGS

const val GET_TAGS_BY_PROJECT = "getTagsByProject"
const val SET_TASK_TAGS = "setTaskTags"
const val GET_TASK_TAGS = "getTaskTags"

//COMMENTS



