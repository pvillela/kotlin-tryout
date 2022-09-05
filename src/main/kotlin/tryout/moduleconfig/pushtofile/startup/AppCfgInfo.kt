package tryout.moduleconfig.pushtofile.startup

data class AppCfgInfo (
	val x: String,
	val y: Int,
)

fun getAppConfiguration(): AppCfgInfo {
	return AppCfgInfo(
		"xxx",
		42,
	)
}
