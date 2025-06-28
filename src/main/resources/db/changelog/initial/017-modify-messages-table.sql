-- Миграция для обновления таблицы messages
-- Добавляем новые столбцы для поддержки чатов и рассылок

-- Создаем перечисление для типов сообщений, если оно еще не существует
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'message_type_enum') THEN
            CREATE TYPE message_type_enum AS ENUM('DIRECT', 'ADMIN_BROADCAST', 'SUPPORT_REQUEST', 'SYSTEM_DECLINE');
        END IF;
    END$$;

-- Добавляем новые столбцы в таблицу messages, если они еще не существуют
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'messages' AND column_name = 'chat_id') THEN
            ALTER TABLE messages ADD COLUMN chat_id VARCHAR(255) NOT NULL DEFAULT '';
        END IF;

        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'messages' AND column_name = 'message_type') THEN
            ALTER TABLE messages ADD COLUMN message_type message_type_enum NOT NULL DEFAULT 'DIRECT';
        END IF;

        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'messages' AND column_name = 'is_broadcast') THEN
            ALTER TABLE messages ADD COLUMN is_broadcast BOOLEAN NOT NULL DEFAULT FALSE;
        END IF;

        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'messages' AND column_name = 'broadcast_responded_by') THEN
            ALTER TABLE messages ADD COLUMN broadcast_responded_by BIGINT NULL;
        END IF;

        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'messages' AND column_name = 'is_system_message') THEN
            ALTER TABLE messages ADD COLUMN is_system_message BOOLEAN NOT NULL DEFAULT FALSE;
        END IF;

        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'messages' AND column_name = 'original_message_id') THEN
            ALTER TABLE messages ADD COLUMN original_message_id BIGINT NULL;
        END IF;
    END$$;

-- Добавляем внешний ключ для broadcast_responded_by, если он еще не существует
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_broadcast_responded_by') THEN
            ALTER TABLE messages
                ADD CONSTRAINT fk_broadcast_responded_by
                    FOREIGN KEY (broadcast_responded_by) REFERENCES users(id) ON DELETE SET NULL;
        END IF;
    END$$;

-- Добавляем внешний ключ для original_message_id, если он еще не существует
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_original_message') THEN
            ALTER TABLE messages
                ADD CONSTRAINT fk_original_message
                    FOREIGN KEY (original_message_id) REFERENCES messages(id) ON DELETE SET NULL;
        END IF;
    END$$;

-- Генерируем chat_id для существующих сообщений, только если есть записи с пустым chat_id
UPDATE messages
SET chat_id = CONCAT('chat_',
                     LEAST(sender_id, recipient_id),
                     '_',
                     GREATEST(sender_id, recipient_id))
WHERE chat_id = '';

-- Создаем индексы для улучшения производительности, если они еще не существуют
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_messages_chat_id') THEN
            CREATE INDEX idx_messages_chat_id ON messages(chat_id);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_messages_message_type') THEN
            CREATE INDEX idx_messages_message_type ON messages(message_type);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_messages_broadcast') THEN
            CREATE INDEX idx_messages_broadcast ON messages(is_broadcast, broadcast_responded_by);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_messages_system') THEN
            CREATE INDEX idx_messages_system ON messages(is_system_message);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_messages_created_at') THEN
            CREATE INDEX idx_messages_created_at ON messages(created_at);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_messages_user_messages') THEN
            CREATE INDEX idx_messages_user_messages ON messages(sender_id, recipient_id, created_at);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_messages_unread') THEN
            CREATE INDEX idx_messages_unread ON messages(recipient_id, is_read, created_at);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_messages_active_broadcasts') THEN
            CREATE INDEX idx_messages_active_broadcasts ON messages(is_broadcast, broadcast_responded_by, created_at);
        END IF;
    END$$;

-- Добавляем комментарии к таблице и столбцам
COMMENT ON TABLE messages IS 'Таблица сообщений с поддержкой чатов и рассылок';
COMMENT ON COLUMN messages.chat_id IS 'Идентификатор чата между пользователями';
COMMENT ON COLUMN messages.message_type IS 'Тип сообщения';
COMMENT ON COLUMN messages.is_broadcast IS 'Флаг, указывающий является ли сообщение рассылкой';
COMMENT ON COLUMN messages.broadcast_responded_by IS 'ID пользователя, ответившего на рассылку';
COMMENT ON COLUMN messages.is_system_message IS 'Флаг, указывающий является ли сообщение системным';
COMMENT ON COLUMN messages.original_message_id IS 'ID оригинального сообщения';