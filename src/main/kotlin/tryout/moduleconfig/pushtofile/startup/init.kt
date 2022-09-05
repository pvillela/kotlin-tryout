/*
 *  Copyright © 2021 Paulo Villela. All rights reserved.
 *  Use of this source code is governed by the Apache 2.0 license
 *  that can be found in the LICENSE file.
 */

package tryout.moduleconfig.pushtofile.startup

import tryout.moduleconfig.pushtofile.fwk.getAppConfiguration
import tryout.moduleconfig.pushtofile.foosfl.barBfAdapterCfgSrc
import tryout.moduleconfig.pushtofile.foosfl.fooSflAdapterCfgSrc

fun initialize()  {
	val c = ::getAppConfiguration
	fooSflAdapterCfgSrc.set(c)
	barBfAdapterCfgSrc.set(c)
}
