export const environment = {
  production: true,
  useMock: false, // Siempre false en producción para que no use simulaciones
  apiUrl: 'https://tu-monolito.com',
  mockUrl: {
    login: 'assets/mocks/login-mock.json',
    vacation: 'assets/mocks/vacation-mock.json',
    birthday: 'assets/mocks/birthday-mock.json',
    disabilities: 'assets/mocks/disabilities-mock.json',
    calamity: 'assets/mocks/calamity-mock.json',
    birthdayLeader: 'assets/mocks/birthday-leader-mock.json',
    calamityLeader: 'assets/mocks/calamity-leader-mock.json',
    vacationLeader: 'assets/mocks/vacation-leader-mock.json',
    disabilitiesLeader: 'assets/mocks/disabilities-leader-mock.json'
  },
  endpoint: {
    login: '/auth/login',
    vacation: '/employee/vacation',
    birthday: '/employee/birthday',
    disabilities: '/employee/disabilities',
    calamity: '/employee/calamity',
    birthdayLeader: '/birthday-leader/requests',
    calamityLeader: '/calamity-leader/requests',
    vacationLeader: '/vacation-leader/requests',
    disabilitiesLeader: '/disabilities-leader/requests'
  }
};
