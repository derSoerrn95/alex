import {Project} from '../../../src/js/entities/project';
import {User} from '../../../src/js/entities/user';

describe('SessionService', () => {
    let EventBus;
    let SessionService;

    let project;
    let user;

    beforeEach(angular.mock.module('ALEX'));
    beforeEach(angular.mock.inject((_EventBus_, _SessionService_) => {
        EventBus = _EventBus_;
        SessionService = _SessionService_;

        project = new Project({id: 1});
        user = new User({id: 1});
    }));

    afterEach(() => {
        sessionStorage.removeItem('project');
        sessionStorage.removeItem('user');
        sessionStorage.removeItem('jwt');
    });

    it('should save a project in the sessionStorage and emit the project:opened event', () => {
        EventBus.on('project:opened', (evt, data) => {
            expect(data.project.id).toEqual(project.id);
        });
        SessionService.saveProject(project);
        expect(sessionStorage.getItem('project')).toBeDefined();
    });


    it('should save a user in the sessionStorage and emit the user:loggedIn event', () => {
        EventBus.on('user:loggedIn', (evt, data) => {
            expect(data.user.id).toEqual(user.id);
        });
        SessionService.saveUser(user);
        expect(sessionStorage.getItem('user')).toBeDefined();
    });

    it('should get the instance of the project', () => {
        SessionService.saveProject(project);
        const p = SessionService.getProject();
        expect(p instanceof Project).toBeTruthy();
        expect(p.id).toEqual(project.id);
    });

    it('should get the instance of the user', () => {
        SessionService.saveUser(user);
        const u = SessionService.getUser();
        expect(u instanceof User).toBeTruthy();
        expect(u.id).toEqual(user.id);
    });

    it('should remove the project in the session', () => {
        SessionService.saveProject(project);
        SessionService.removeProject();
        expect(sessionStorage.getItem('project')).toBeNull();
    });

    it('should remove the user in the session', () => {
        SessionService.saveUser(user);
        SessionService.removeUser();
        expect(sessionStorage.getItem('user')).toBeNull();
        expect(sessionStorage.getItem('jwt')).toBeNull();
    });

    it('should return null if no project is in the session', () => {
        expect(SessionService.getProject()).toBeNull();
    });

    it('should return null if no user is in the session', () => {
        expect(SessionService.getUser()).toBeNull();
    });
});
