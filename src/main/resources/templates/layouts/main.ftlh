<#macro layout title="" active="" pageStyles="">
<!DOCTYPE html>
<html lang="ru" class="h-full">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="${pageDescription!'Arannati - Премиальная космецевтика и БАДы'}">
    <title>${pageTitle!'Arannati'}</title>

    <!-- Favicon -->
    <link rel="icon" type="image/x-icon" href="/static/images/favicon.ico">

    <!-- CSS Libraries -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.2/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>

    <!-- Custom CSS -->
    <link href="/static/css/main.css" rel="stylesheet">
    <link href="/static/css/components.css" rel="stylesheet">
    <link href="/static/css/auth.css" rel="stylesheet">

    <!-- Additional CSS for specific pages -->
    <#if additionalCSS??>
        <#list additionalCSS as css>
            <link href="${css}" rel="stylesheet">
        </#list>
    </#if>
</head>
<body class="h-full bg-gray-50">
<!-- Header -->
<#include "../fragments/header.ftlh">

<!-- Flash Messages -->
<#include "../fragments/flash-messages.ftlh">

<!-- Main Content -->
<main class="flex-1">
    <#nested>
</main>

<!-- Footer -->
<#include "../fragments/footer.ftlh">

<!-- JavaScript Libraries -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.2/js/bootstrap.bundle.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

<!-- Custom JavaScript -->
<script src="/static/js/utils/config.js"></script>
<script src="/static/js/utils/validator.js"></script>
<script src="/static/js/utils/api.js"></script>
<script src="/static/js/components/form-validator.js"></script>
<script src="/static/js/components/file-uploader.js"></script>
<script src="/static/js/components/toast-notifications.js"></script>
<script src="/static/js/main.js"></script>
<script src="/static/js/components-init.js"></script>

<!-- Page-specific JavaScript -->
<#if additionalJS??>
    <#list additionalJS as js>
        <script src="${js}"></script>
    </#list>
</#if>

<!-- Inline JavaScript -->
<#if inlineJS??>
    <script>
        ${inlineJS}
    </script>
</#if>
</body>
</html>
</#macro>