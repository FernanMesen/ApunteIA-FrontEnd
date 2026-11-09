package ApunteIA.FrontEnd.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import ApunteIA.FrontEnd.domain.model.CourseInfo

data class CustomCourseDto(
    val id: Long,
    val code: String,
    val name: String,
    val description: String
)

@Singleton
class CustomSubjectStore @Inject constructor(
    @ApplicationContext private val context: Context?
) {
    private val prefs = context?.getSharedPreferences("apunteia_custom_subjects", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getCustomCourses(): List<CourseInfo> {
        val json = prefs?.getString("custom_courses_list", null) ?: return emptyList()
        val type = object : TypeToken<List<CustomCourseDto>>() {}.type
        val list: List<CustomCourseDto> = runCatching { gson.fromJson<List<CustomCourseDto>>(json, type) }.getOrDefault(emptyList())
        return list.map { CourseInfo(id = it.id, code = it.code, name = it.name, description = it.description) }
    }

    fun addCustomCourse(name: String, code: String, description: String) {
        val current = getCustomCourses().toMutableList()
        val newId = 9000L + current.size
        current.add(CourseInfo(id = newId, code = code.ifBlank { "SUB-$newId" }, name = name, description = description))
        val dtos = current.map { CustomCourseDto(it.id, it.code, it.name, it.description) }
        prefs?.edit()?.putString("custom_courses_list", gson.toJson(dtos))?.apply()
    }
}
