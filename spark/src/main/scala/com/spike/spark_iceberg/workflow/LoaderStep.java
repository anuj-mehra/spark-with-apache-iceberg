package com.spike.spark_iceberg.workflow;

import scala.Unit;

public interface LoaderStep<I> extends ETLStep<I, Unit> {
}
