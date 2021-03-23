export const authentication = {
  BASIC: "basic",
  BEARER: "bearer"
};

export class Auth {

  constructor(authType, value) {
    this.authType = authType;
    this.value = value;
  }

}
