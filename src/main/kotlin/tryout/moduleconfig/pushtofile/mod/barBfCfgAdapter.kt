/*
 * Copyright © 2022 Paulo Villela. All rights reserved.
 * Use of this source code is governed by the MIT license
 * that can be found in the LICENSE file.
 */

package tryout.moduleconfig.pushtofile.mod

import tryout.moduleconfig.pushtofile.fwk.AppCfgInfo
import tryout.moduleconfig.pushtofile.fwk.makeConfigSource

fun barBfCfgAdapter(appCfg: AppCfgInfo): BarBfCfgInfo {
	return BarBfCfgInfo(
		appCfg.y,
	)
}

val barBfAdapterCfgSrc = run {
	val cfgSrc = makeConfigSource<AppCfgInfo>();
	barBfCfgSrc.set({
		barBfCfgAdapter(cfgSrc.get());
	});
	cfgSrc
}
