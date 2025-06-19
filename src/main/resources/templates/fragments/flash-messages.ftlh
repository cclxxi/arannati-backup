<#if successMessage??>
    <div class="alert alert-success alert-dismissible fade show m-4" role="alert">
        <i class="fas fa-check-circle me-2"></i>
        ${successMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</#if>

<#if errorMessage??>
    <div class="alert alert-danger alert-dismissible fade show m-4" role="alert">
        <i class="fas fa-exclamation-circle me-2"></i>
        ${errorMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</#if>

<#if warningMessage??>
    <div class="alert alert-warning alert-dismissible fade show m-4" role="alert">
        <i class="fas fa-exclamation-triangle me-2"></i>
        ${warningMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</#if>

<#if infoMessage??>
    <div class="alert alert-info alert-dismissible fade show m-4" role="alert">
        <i class="fas fa-info-circle me-2"></i>
        ${infoMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</#if>