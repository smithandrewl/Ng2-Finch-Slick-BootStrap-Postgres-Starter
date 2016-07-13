declare module data {

    export interface IEvent {
        IPAddress: string;
        Section:   string;
        Timestamp: string;
        User:      number;
        Result:    string;
        Action:    string;
        Severity:  string;
        Type:      string;
    }

    export interface IUser {
        userId:   number;
        username: string;
        hash:     string;
        isAdmin:  boolean;
    }

}

