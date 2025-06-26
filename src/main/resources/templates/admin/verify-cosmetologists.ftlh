<#assign pageTitle = "Arannati - Верификация косметологов">
<#assign pageDescription = "Верификация косметологов в административной панели Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <!-- Page Header -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h1 class="text-2xl font-bold text-gray-800">Верификация косметологов</h1>
                <a href="/dashboard" class="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                    <i class="fas fa-arrow-left mr-2"></i> Назад к панели
                </a>
            </div>

            <div class="bg-blue-50 border-l-4 border-blue-500 p-4">
                <div class="flex">
                    <div class="flex-shrink-0">
                        <i class="fas fa-info-circle text-blue-500"></i>
                    </div>
                    <div class="ml-3">
                        <p class="text-sm text-blue-700">
                            Здесь вы можете проверять и подтверждать заявки на регистрацию косметологов. Внимательно проверяйте предоставленные документы и информацию перед подтверждением.
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Verification Requests -->
        <div class="bg-white shadow-md rounded-lg overflow-hidden mb-6">
            <div class="px-6 py-4 border-b border-gray-200">
                <h2 class="text-lg font-semibold text-gray-800">Заявки на верификацию</h2>
            </div>

            <#if cosmetologists?size == 0>
                <div class="p-6 text-center text-gray-500">
                    <i class="fas fa-check-circle text-green-500 text-5xl mb-4"></i>
                    <p>Нет ожидающих заявок на верификацию</p>
                </div>
            <#else>
                <div class="overflow-x-auto">
                    <table class="min-w-full divide-y divide-gray-200">
                        <thead class="bg-gray-50">
                            <tr>
                                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Косметолог
                                </th>
                                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Дата регистрации
                                </th>
                                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Специализация
                                </th>
                                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Документы
                                </th>
                                <th scope="col" class="relative px-6 py-3">
                                    <span class="sr-only">Действия</span>
                                </th>
                            </tr>
                        </thead>
                        <tbody class="bg-white divide-y divide-gray-200">
                            <#list cosmetologists as cosmetologist>
                                <tr>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="flex items-center">
                                            <div class="flex-shrink-0 h-10 w-10">
                                                <#if cosmetologist.profileImage?? && cosmetologist.profileImage != "">
                                                    <img class="h-10 w-10 rounded-full object-cover" src="${cosmetologist.profileImage}" alt="${cosmetologist.firstName} ${cosmetologist.lastName}">
                                                <#else>
                                                    <div class="h-10 w-10 rounded-full bg-gray-200 flex items-center justify-center">
                                                        <i class="fas fa-user text-gray-500"></i>
                                                    </div>
                                                </#if>
                                            </div>
                                            <div class="ml-4">
                                                <div class="text-sm font-medium text-gray-900">
                                                    ${cosmetologist.firstName} ${cosmetologist.lastName}
                                                </div>
                                                <div class="text-sm text-gray-500">
                                                    ${cosmetologist.email}
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${cosmetologist.registrationDate}</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${cosmetologist.specialization}</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <button class="view-documents px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-xs font-semibold" data-id="${cosmetologist.id}">
                                            <i class="fas fa-file-alt mr-1"></i> Просмотреть
                                        </button>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                        <button class="approve-cosmetologist text-green-600 hover:text-green-900 mr-3" data-id="${cosmetologist.id}" data-name="${cosmetologist.firstName} ${cosmetologist.lastName}">
                                            <i class="fas fa-check-circle"></i> Подтвердить
                                        </button>
                                        <button class="decline-cosmetologist text-red-600 hover:text-red-900" data-id="${cosmetologist.id}" data-name="${cosmetologist.firstName} ${cosmetologist.lastName}">
                                            <i class="fas fa-times-circle"></i> Отклонить
                                        </button>
                                    </td>
                                </tr>
                            </#list>
                        </tbody>
                    </table>
                </div>
            </#if>
        </div>

        <!-- Recently Verified Cosmetologists -->
        <div class="bg-white shadow-md rounded-lg overflow-hidden">
            <div class="px-6 py-4 border-b border-gray-200">
                <h2 class="text-lg font-semibold text-gray-800">Недавно верифицированные косметологи</h2>
            </div>

            <#if recentlyVerified?size == 0>
                <div class="p-6 text-center text-gray-500">
                    <p>Нет недавно верифицированных косметологов</p>
                </div>
            <#else>
                <div class="overflow-x-auto">
                    <table class="min-w-full divide-y divide-gray-200">
                        <thead class="bg-gray-50">
                            <tr>
                                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Косметолог
                                </th>
                                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Дата верификации
                                </th>
                                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Специализация
                                </th>
                                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Статус
                                </th>
                            </tr>
                        </thead>
                        <tbody class="bg-white divide-y divide-gray-200">
                            <#list recentlyVerified as cosmetologist>
                                <tr>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="flex items-center">
                                            <div class="flex-shrink-0 h-10 w-10">
                                                <#if cosmetologist.profileImage?? && cosmetologist.profileImage != "">
                                                    <img class="h-10 w-10 rounded-full object-cover" src="${cosmetologist.profileImage}" alt="${cosmetologist.firstName} ${cosmetologist.lastName}">
                                                <#else>
                                                    <div class="h-10 w-10 rounded-full bg-gray-200 flex items-center justify-center">
                                                        <i class="fas fa-user text-gray-500"></i>
                                                    </div>
                                                </#if>
                                            </div>
                                            <div class="ml-4">
                                                <div class="text-sm font-medium text-gray-900">
                                                    ${cosmetologist.firstName} ${cosmetologist.lastName}
                                                </div>
                                                <div class="text-sm text-gray-500">
                                                    ${cosmetologist.email}
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${cosmetologist.verificationDate}</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${cosmetologist.specialization}</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                                            Верифицирован
                                        </span>
                                    </td>
                                </tr>
                            </#list>
                        </tbody>
                    </table>
                </div>
            </#if>
        </div>
    </div>

    <!-- Documents Modal -->
    <div id="documents-modal" class="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center hidden z-50">
        <div class="bg-white rounded-lg shadow-xl max-w-4xl w-full mx-4">
            <div class="px-6 py-4 border-b">
                <div class="flex items-center justify-between">
                    <h3 id="documents-title" class="text-lg font-medium text-gray-900">Документы косметолога</h3>
                    <button id="close-documents-modal" class="text-gray-400 hover:text-gray-500">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            </div>
            <div class="px-6 py-4 max-h-[70vh] overflow-y-auto">
                <div id="documents-content" class="space-y-6">
                    <!-- Documents will be loaded here -->
                </div>
            </div>
            <div class="px-6 py-4 border-t flex justify-end">
                <button id="close-documents-btn" class="bg-gray-200 text-gray-700 px-4 py-2 rounded hover:bg-gray-300">
                    Закрыть
                </button>
            </div>
        </div>
    </div>

    <!-- Decline Reason Modal -->
    <div id="decline-modal" class="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center hidden z-50">
        <div class="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
            <div class="px-6 py-4 border-b">
                <div class="flex items-center justify-between">
                    <h3 id="decline-title" class="text-lg font-medium text-gray-900">Причина отклонения</h3>
                    <button id="close-decline-modal" class="text-gray-400 hover:text-gray-500">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            </div>
            <form id="decline-form">
                <div class="px-6 py-4">
                    <input type="hidden" id="decline-cosmetologist-id">
                    <div class="mb-4">
                        <label for="decline-reason" class="block text-sm font-medium text-gray-700 mb-1">Укажите причину отклонения заявки</label>
                        <textarea id="decline-reason" name="reason" rows="4" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" required></textarea>
                    </div>
                </div>
                <div class="px-6 py-4 border-t flex justify-end">
                    <button type="button" id="cancel-decline" class="bg-gray-200 text-gray-700 px-4 py-2 rounded hover:bg-gray-300 mr-2">
                        Отмена
                    </button>
                    <button type="submit" class="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700">
                        Отклонить заявку
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // View documents
            const viewDocumentsBtns = document.querySelectorAll('.view-documents');
            const documentsModal = document.getElementById('documents-modal');
            const closeDocumentsModal = document.getElementById('close-documents-modal');
            const closeDocumentsBtn = document.getElementById('close-documents-btn');
            const documentsTitle = document.getElementById('documents-title');
            const documentsContent = document.getElementById('documents-content');

            viewDocumentsBtns.forEach(button => {
                button.addEventListener('click', () => {
                    const cosmetologistId = button.getAttribute('data-id');

                    // Fetch documents
                    fetch('/admin/cosmetologists/' + cosmetologistId + '/documents')
                        .then(response => response.json())
                        .then(data => {
                            documentsTitle.textContent = 'Документы косметолога: ' + data.cosmetologistName;

                            // Clear previous content
                            documentsContent.innerHTML = '';

                            // Add documents
                            if (data.documents && data.documents.length > 0) {
                                data.documents.forEach(doc => {
                                    const docElement = document.createElement('div');
                                    docElement.className = 'border rounded-lg overflow-hidden';

                                    let docContent = 
                                        '<div class="px-4 py-2 bg-gray-50 border-b">' +
                                        '<h4 class="font-medium">' + doc.title + '</h4>' +
                                        '</div>' +
                                        '<div class="p-4">';

                                    if (doc.type === 'image') {
                                        docContent += 
                                            '<img src="' + doc.url + '" alt="' + doc.title + '" class="max-w-full h-auto rounded">';
                                    } else if (doc.type === 'pdf') {
                                        docContent += 
                                            '<div class="flex items-center">' +
                                            '<i class="fas fa-file-pdf text-red-500 text-3xl mr-3"></i>' +
                                            '<div>' +
                                            '<p class="text-sm text-gray-700">' + (doc.description || 'PDF документ') + '</p>' +
                                            '<a href="' + doc.url + '" target="_blank" class="text-blue-600 hover:text-blue-800 text-sm">' +
                                            'Открыть документ <i class="fas fa-external-link-alt ml-1"></i>' +
                                            '</a>' +
                                            '</div>' +
                                            '</div>';
                                    } else {
                                        docContent += 
                                            '<div class="flex items-center">' +
                                            '<i class="fas fa-file text-gray-500 text-3xl mr-3"></i>' +
                                            '<div>' +
                                            '<p class="text-sm text-gray-700">' + (doc.description || 'Документ') + '</p>' +
                                            '<a href="' + doc.url + '" target="_blank" class="text-blue-600 hover:text-blue-800 text-sm">' +
                                            'Открыть документ <i class="fas fa-external-link-alt ml-1"></i>' +
                                            '</a>' +
                                            '</div>' +
                                            '</div>';
                                    }

                                    docContent += '</div>';

                                    docElement.innerHTML = docContent;
                                    documentsContent.appendChild(docElement);
                                });
                            } else {
                                documentsContent.innerHTML = 
                                    '<div class="text-center text-gray-500">' +
                                    '<p>Документы не найдены</p>' +
                                    '</div>';
                            }

                            // Show modal
                            documentsModal.classList.remove('hidden');
                        })
                        .catch(error => {
                            console.error('Error fetching documents:', error);
                            alert('Ошибка при загрузке документов');
                        });
                });
            });

            // Close documents modal
            closeDocumentsModal.addEventListener('click', () => {
                documentsModal.classList.add('hidden');
            });

            closeDocumentsBtn.addEventListener('click', () => {
                documentsModal.classList.add('hidden');
            });

            // Approve cosmetologist
            const approveBtns = document.querySelectorAll('.approve-cosmetologist');

            approveBtns.forEach(button => {
                button.addEventListener('click', () => {
                    const cosmetologistId = button.getAttribute('data-id');
                    const cosmetologistName = button.getAttribute('data-name');

                    if (confirm('Вы уверены, что хотите подтвердить косметолога "' + cosmetologistName + '"?')) {
                        fetch('/admin/cosmetologists/' + cosmetologistId + '/approve', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        })
                        .then(response => {
                            if (response.ok) {
                                alert('Косметолог успешно подтвержден');
                                window.location.reload();
                            } else {
                                alert('Ошибка при подтверждении косметолога');
                            }
                        })
                        .catch(error => {
                            console.error('Error approving cosmetologist:', error);
                            alert('Ошибка при подтверждении косметолога');
                        });
                    }
                });
            });

            // Decline cosmetologist
            const declineBtns = document.querySelectorAll('.decline-cosmetologist');
            const declineModal = document.getElementById('decline-modal');
            const closeDeclineModal = document.getElementById('close-decline-modal');
            const cancelDecline = document.getElementById('cancel-decline');
            const declineForm = document.getElementById('decline-form');
            const declineCosmetologistId = document.getElementById('decline-cosmetologist-id');
            const declineTitle = document.getElementById('decline-title');

            declineBtns.forEach(button => {
                button.addEventListener('click', () => {
                    const cosmetologistId = button.getAttribute('data-id');
                    const cosmetologistName = button.getAttribute('data-name');

                    declineCosmetologistId.value = cosmetologistId;
                    declineTitle.textContent = 'Причина отклонения заявки: ' + cosmetologistName;

                    declineModal.classList.remove('hidden');
                });
            });

            // Close decline modal
            closeDeclineModal.addEventListener('click', () => {
                declineModal.classList.add('hidden');
            });

            cancelDecline.addEventListener('click', () => {
                declineModal.classList.add('hidden');
            });

            // Submit decline form
            declineForm.addEventListener('submit', (e) => {
                e.preventDefault();

                const cosmetologistId = declineCosmetologistId.value;
                const reason = document.getElementById('decline-reason').value.trim();

                if (!reason) {
                    alert('Пожалуйста, укажите причину отклонения');
                    return;
                }

                fetch('/admin/cosmetologists/' + cosmetologistId + '/decline', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'reason=' + encodeURIComponent(reason)
                })
                .then(response => {
                    if (response.ok) {
                        alert('Заявка косметолога отклонена');
                        window.location.reload();
                    } else {
                        alert('Ошибка при отклонении заявки косметолога');
                    }
                })
                .catch(error => {
                    console.error('Error declining cosmetologist:', error);
                    alert('Ошибка при отклонении заявки косметолога');
                });
            });
        });
    </script>
</@layout>
