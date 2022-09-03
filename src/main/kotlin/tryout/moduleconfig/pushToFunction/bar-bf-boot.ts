/*
 * Copyright © 2022 Paulo Villela. All rights reserved.
 * Use of this source code is governed by the MIT license
 * that can be found in the LICENSE file.
 */

import { AppCfgInfo} from "./app-cfg";
import { barBfC, BarBfT, BarBfCfgInfo } from "./bar-bf";

function adapter(appCfg: AppCfgInfo): BarBfCfgInfo {
  return {
    z: appCfg.y
  };
}

export function barBfBoot(appCfg: () => AppCfgInfo): BarBfT {
  return barBfC({ get:  () => adapter(appCfg()) })
}
