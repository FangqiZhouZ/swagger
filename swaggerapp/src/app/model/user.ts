export class User {
    constructor(
      public id = 0,
      public userId = '',
      public firstName = '',
      public lastName = '',
      public username = '',
      public email = '',
      public lastLoginDate = null,
      public logInDateDisplay = null,
      public joinDate = null,
      public profileImageUrl = '',
      public active = false,
      public notLocked = false,
      public role = '',
      public authorities = []) {}
  
  }
  