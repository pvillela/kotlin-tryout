/*
 * Copyright © 2022 Paulo Villela. All rights reserved.
 * Use of this source code is governed by the MIT license
 * that can be found in the LICENSE file.
 */

package tryout.moduleconfig.pushtofunction.mod

import tryout.moduleconfig.pushtofunction.fwk.AppCfgInfo

fun barBfCfgAdapter(appCfg: AppCfgInfo): BarBfCfgInfo {
	return BarBfCfgInfo(
		appCfg.y,
	)
}

fun barBfBoot(appCfg: () -> AppCfgInfo): BarBfT {
	val barBfCfgSrc = BarBfCfgSrc(
		{ barBfCfgAdapter(appCfg()) }
	)
	return barBfC(barBfCfgSrc)
}
