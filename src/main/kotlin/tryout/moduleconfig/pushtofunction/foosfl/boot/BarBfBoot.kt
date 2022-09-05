/*
 * Copyright © 2022 Paulo Villela. All rights reserved.
 * Use of this source code is governed by the MIT license
 * that can be found in the LICENSE file.
 */

package tryout.moduleconfig.pushtofunction.foosfl.boot

import tryout.moduleconfig.pushtofunction.foosfl.BarBfCfgInfo
import tryout.moduleconfig.pushtofunction.foosfl.BarBfCfgSrc
import tryout.moduleconfig.pushtofunction.foosfl.BarBfT
import tryout.moduleconfig.pushtofunction.foosfl.barBfC
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
