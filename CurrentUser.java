package dit.hua.gr.greenride.core.security;

import dit.hua.gr.greenride.core.model.PersonType;

public record CurrentUser(long id, String emailAddress, PersonType type) {}