import { initializeApp } from "firebase-admin/app";
import { getFirestore } from "firebase-admin/firestore";
import { setGlobalOptions } from "firebase-functions";
import { onRequest } from "firebase-functions/https";
import * as logger from "firebase-functions/logger";
import { beforeUserCreated } from "firebase-functions/v2/identity";

initializeApp();

const db = getFirestore();
const usersCollection = db.collection("users");

setGlobalOptions({ maxInstances: 10 });

const getUserDocumentData = (user) => {
    const doc = {};

    for (const field of [
        "email",
        "emailVerified",
        "displayName",
        "photoURL",
        "phoneNumber",
        "disabled",
        "creationTime",
    ]) {
        const fieldValue = user[field];
        if (fieldValue) {
            doc[field] = fieldValue;
        }
    }

    return doc;
};

export const createUserDocument = beforeUserCreated(async (event) => {
    const user = event.data;
    const data = getUserDocumentData(user);
    await usersCollection.doc(user.uid).set(data);
});
