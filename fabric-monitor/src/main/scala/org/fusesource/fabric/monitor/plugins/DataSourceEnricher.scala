package org.fusesource.fabric.monitor.plugins

import jmx.{MBeanAttributePollDTO, JmxConstants}
import org.fusesource.fabric.monitor.api.DataSourceDTO

object DataSourceEnricher {
  var enrichers: List[DataSourceEnricher] = List(new DefaultDataSourceEnricher)

  def apply(dto: DataSourceDTO): DataSourceDTO = {
    var d = dto
    for (e <- enrichers) {
      d = e(d)
    }
    d
  }
}

/**
 * Enriches a given data source
 */
abstract class DataSourceEnricher extends Function1[DataSourceDTO, DataSourceDTO] {
}

class DefaultDataSourceEnricher extends DataSourceEnricher {

  def apply(source: DataSourceDTO): DataSourceDTO = {

    def memory_key_enrich(key:String):Unit = {
      key match {
        case "used" => source.name = "Used "+source.name
        case "max" => source.name = "Max "+source.name
        case "committed" => source.name = "Allocated "+source.name
        case _ =>
      }
    }

    source.poll match {
      case x:MBeanAttributePollDTO =>
        (x.mbean, x.attribute, x.key) match {

        case ("java.lang:name=CMS Old Gen,type=MemoryPool", "Usage", key) =>
          // case "jvm.heap.cms_old_gen" =>
          source.name = "Old Gen Space"
          source.description = "This space holds tenured objects which are not expected to be garbage collected often."
          memory_key_enrich(key)

        case ("java.lang:name=Par Survivor Space,type=MemoryPool", "Usage", key) =>
          // case "jvm.heap.par_survivor" =>
          source.name = "Par Survivor Space"
          source.description = "This space holds object which survive past the eden pool"
          memory_key_enrich(key)

        case ("java.lang:name=Par Eden Space,type=MemoryPool", "Usage", key) =>
          // case "jvm.heap.par_eden" =>
          source.name = "Par Eden Space"
          source.description = "This space is where objects are first allocated."
          memory_key_enrich(key)

        case ("java.lang:type=Memory", "HeapMemoryUsage", key) =>
          // case "jvm.heap.committed" =>
          source.name = "All Heap Pools"
          source.description = "The the amount of memory allocated for all the heap pools."
          memory_key_enrich(key)

        //
        // Non heap spaces.
        //
        case ("java.lang:name=CMS Perm Gen,type=MemoryPool", "Usage", key) =>
          // case "jvm.non_heap.cms_perm" =>
          source.name = "CMS Perm Gen Space"
          source.description = "The space containing all the reflective data of the virtual machine itself, such as class and method objects."
          memory_key_enrich(key)

        case ("java.lang:name=Code Cache,type=MemoryPool", "Usage", key) =>
          // case "jvm.non_heap.code" =>
          source.name = "Code Cache Space"
          source.description = "The space containing memory that is used for compilation and storage of native code."
          memory_key_enrich(key)

        case ("java.lang:type=Memory", "NonHeapMemoryUsage", key) =>
          // case "jvm.non_heap.summary" =>
          source.name = "All Non-Heap Pools"
          source.description = "The the amount of memory being used in all the non-heap pools."
          memory_key_enrich(key)

        // other stuff
        case ("java.lang:type=Threading", "ThreadCount", key) =>
          // case "jvm.threading.count" =>
          source.name = "Running Threads"
          source.description = "The number of running threads being used by the JVM."

        case ("java.lang:type=OperatingSystem", "OpenFileDescriptorCount", key) =>
          // case "jvm.os.fd" =>
          source.name = "Open File Descriptors"
          source.description = "The number of open file descriptors."

        case _ =>
      }
    }
    source
  }
}