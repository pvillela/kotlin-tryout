/*
 *  Copyright © 2021 Paulo Villela. All rights reserved.
 *  Use of this source code is governed by the Apache 2.0 license
 *  that can be found in the LICENSE file.
 */

package tryout.moduleconfig.pushtovar.fwk

import tryout.moduleconfig.pushtovar.startup.AppCfgInfo

interface CfgSrc<T> {
	fun set(infoSrc: () -> T)
	fun get(): T
}


private class CfgSrcImpl<T>: CfgSrc<T> {
	private var cfgSrc: (() -> T)? = null

	override fun set(infoSrc: () -> T) {
		this.cfgSrc = infoSrc
	}

	override fun get(): T {
		val cfgSrc = this.cfgSrc
		if (cfgSrc == null) {
			throw ConfigurationException("Module used before being initialized")
		}
		return cfgSrc()
	}
}

fun <T>makeConfigSource(adapter: ((appCfg: AppCfgInfo) -> T)? = null): CfgSrc<T> {
	return CfgSrcImpl<T>()
}
