package cloud.kubelet.foundation.core

fun <T, R : Comparable<R>> Collection<T>.sortedBy(order: SortOrder, selector: (T) -> R?): List<T> =
  if (order == SortOrder.Ascending) {
    sortedBy(selector)
  } else {
    sortedByDescending(selector)
  }
