package com.cisdi.steel.module.job.drt.writer;

import com.cisdi.steel.module.job.drt.dto.DrtWriterDTO;

public interface IDrtWriter<T> {
    T drtWriter(DrtWriterDTO drtWriterDTO);
}
