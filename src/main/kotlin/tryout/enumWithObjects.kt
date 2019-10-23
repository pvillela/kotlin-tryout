package tryout

sealed class ActionItemType

object LoadPurse : ActionItemType() {
    const val v = "LoadPurse"
}

object HotList : ActionItemType() {
    const val v = "HotList"
}

val actionItemType: ActionItemType = LoadPurse

private const val foo = LoadPurse.v
