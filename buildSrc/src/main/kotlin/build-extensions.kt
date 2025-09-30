import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import dev.kikugie.stonecutter.controller.StonecutterControllerExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.maven
import java.io.File

val Project.mod: ModData get() = ModData(this)
fun Project.prop(key: String): String? = findProperty(key)?.toString()
fun String.upperCaseFirst() = replaceFirstChar { if (it.isLowerCase()) it.uppercaseChar() else it }

fun RepositoryHandler.strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
    forRepository { maven(url) { name = alias } }
    filter { groups.forEach(::includeGroup) }
}

val Project.stonecutterBuild get() = extensions.getByType<StonecutterBuildExtension>()
val Project.stonecutterController get() = extensions.getByType<StonecutterControllerExtension>()

val Project.common
    get() = requireNotNull(stonecutterBuild.node.sibling("common")) {
        "No common project for $project"
    }
val Project.commonProject get() = rootProject.project(stonecutterBuild.current.project)
val Project.commonMod get() = commonProject.mod

val Project.loader: String? get() = prop("loader")

@JvmInline
value class ModData(private val project: Project) {
    val id: String get() = modProp("id")
    val name: String get() = modProp("name")
    val version: String get() = modProp("version")
    val group: String get() = modProp("group")
    val author: String get() = modProp("author")
    val description: String get() = modProp("description")
    val license: String get() = modProp("license")
    val github: String get() = modProp("github")
    val mcVersion: String get() = depOrNull("minecraft") ?: project.stonecutterBuild.current.version
    val aw: String get() = getAwFileName(project.stonecutterBuild)

    fun propOrNull(key: String) = project.prop(key)
    fun prop(key: String) = requireNotNull(propOrNull(key)) { "Missing '$key'" }
    fun modPropOrNull(key: String) = project.prop("mod.$key")
    fun modProp(key: String) = requireNotNull(modPropOrNull(key)) { "Missing 'mod.$key'" }
    fun depOrNull(key: String): String? = project.prop("deps.$key")?.takeIf { it.isNotEmpty() && it != "" }
    fun dep(key: String) = requireNotNull(depOrNull(key)) { "Missing 'deps.$key'" }
}

/**
 * Moves a file or folder to a new destination and optionally deletes the source after moving.
 *
 * @param buildMetaInf The base directory containing the files or folders.
 * @param src The relative path of the source file or folder to move.
 * @param dest The relative path of the destination where the file or folder will be moved.
 * @param delete Specifies whether the source file or folder should be deleted after moving. Default is `true`.
 *
 * <pre>
 * moveAndDeleteFileOrFolder(
 *  file("${layout.buildDirectory.get().toString()}/resources/main/"),
 *      "data/modname/function",
 *      "data/modname/functions"
 *  )
 *  </pre>
 */
fun moveAndDeleteFileOrFolder(
    buildMetaInf: File,
    src: String,
    dest: String,
    delete: Boolean = true
) {
    val srcFile = buildMetaInf.resolve(src)
    val destFile = buildMetaInf.resolve(dest)
    if (srcFile.exists()) {
        srcFile.copyRecursively(destFile, overwrite = true)
        if (delete) {
            srcFile.deleteRecursively()
        }
    }
}

fun getAwFileName(stonecutterBuild: StonecutterBuildExtension): String {
    return "1.21.7.accesswidener"
//    if (stonecutterBuild.eval(stonecutterBuild.current.version, "<1.21.8")) {
//        "1.19.4.accesswidener"
//    } else {
//        "1.21.8.accesswidener"
//    }
}
