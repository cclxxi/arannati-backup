package kz.arannati.arannati.enums;

public enum MessageType {
    DIRECT,           // Прямое сообщение между пользователями
    ADMIN_BROADCAST,  // Рассылка косметологу/пользователю от админа
    SUPPORT_REQUEST,  // Запрос в поддержку/админскую службу
    SYSTEM_DECLINE    // Системное сообщение об отклонении заявки
}
