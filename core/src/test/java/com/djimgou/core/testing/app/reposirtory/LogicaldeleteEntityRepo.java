package com.djimgou.core.testing.app.reposirtory;

import com.djimgou.core.annotations.LogicalDelete;
import com.djimgou.core.repository.BaseJpaRepository;
import com.djimgou.core.testing.app.model.LogicalDeleEntity;

import java.util.UUID;
@LogicalDelete("")
public interface LogicaldeleteEntityRepo extends BaseJpaRepository<LogicalDeleEntity, UUID>  {

}
