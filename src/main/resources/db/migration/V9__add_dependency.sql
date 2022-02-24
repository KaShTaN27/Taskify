INSERT INTO app_user_roles (app_user_id, roles_id)
VALUES (5, 2);

INSERT INTO organization_app_users (organization_id, app_users_id)
VALUES (2, 1), (2, 5);

INSERT INTO app_user_tasks (app_user_id, tasks_id)
VALUES (1, 1), (5, 2), (5, 3);