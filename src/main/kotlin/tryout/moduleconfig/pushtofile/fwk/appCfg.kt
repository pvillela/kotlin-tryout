package tryout.moduleconfig.pushtofile.fwk

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
