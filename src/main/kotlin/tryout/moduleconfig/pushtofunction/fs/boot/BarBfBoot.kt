/*
 * Copyright © 2022 Paulo Villela. All rights reserved.
 * Use of this source code is governed by the MIT license
 * that can be found in the LICENSE file.
 */

package tryout.moduleconfig.pushtofunction.fs.boot

import tryout.moduleconfig.pushtofunction.fs.BarBfCfgInfo
import tryout.moduleconfig.pushtofunction.fs.BarBfCfgSrc
import tryout.moduleconfig.pushtofunction.fs.BarBfT
import tryout.moduleconfig.pushtofunction.fs.barBfC
import tryout.moduleconfig.pushtofunction.fwk.AppCfgInfo

fun barBfBoot(appCfg: () -> AppCfgInfo): BarBfT {
	val barBfCfgSrc = BarBfCfgSrc(
		get = { barBfCfgAdapter(appCfg()) }
	)
	return barBfC(barBfCfgSrc)
}

fun barBfCfgAdapter(appCfg: AppCfgInfo): BarBfCfgInfo {
	return BarBfCfgInfo(
		z = appCfg.y,
	)
}
