package org.kadai.kadai

fun <E> rangeList(list: List<E>, range: IntRange): List<E> {
    val ret = arrayListOf<E>()
    var count = range.first
    val last = range.last
    while(count <= last) {
        if (count >= list.size)
            break
        ret.add(list[count])
        count++
    }
    return ret
}